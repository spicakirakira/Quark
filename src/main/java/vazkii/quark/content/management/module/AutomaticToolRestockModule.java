package vazkii.quark.content.management.module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.WeakHashMap;
import java.util.function.Predicate;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.core.Registry;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.quark.api.event.GatherToolClassesEvent;
import vazkii.quark.base.handler.MiscUtil;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;

@LoadModule(category = ModuleCategory.MANAGEMENT, hasSubscriptions = true, antiOverlap = "inventorytweaks")
public class AutomaticToolRestockModule extends QuarkModule {

	private static final Map<ToolAction, String> ACTION_TO_CLASS = new HashMap<>();
	
	static {
		ACTION_TO_CLASS.put(ToolActions.AXE_DIG, "axe");
		ACTION_TO_CLASS.put(ToolActions.HOE_DIG, "hoe");
		ACTION_TO_CLASS.put(ToolActions.SHOVEL_DIG, "shovel");
		ACTION_TO_CLASS.put(ToolActions.PICKAXE_DIG, "pickaxe");
		ACTION_TO_CLASS.put(ToolActions.SWORD_SWEEP, "sword");
		ACTION_TO_CLASS.put(ToolActions.SHEARS_HARVEST, "shears");
		ACTION_TO_CLASS.put(ToolActions.FISHING_ROD_CAST, "fishing_rod");
	}
	
	private static final WeakHashMap<Player, Stack<Pair<Integer, Integer>>> replacements = new WeakHashMap<>();

	public List<Enchantment> importantEnchants = new ArrayList<>();

	@Config(name = "Important Enchantments",
			description = "Enchantments deemed important enough to have special priority when finding a replacement")
	private List<String> enchantNames = generateDefaultEnchantmentList();

	@Config(description = "Enable replacing your tools with tools of the same type but not the same item")
	private boolean enableLooseMatching = true;

	@Config(description = "Enable comparing enchantments to find a replacement")
	private boolean enableEnchantMatching = true;

	@Config
	private boolean unstackablesOnly = false;

	private Object mutex = new Object();
	
	@Override
	public void configChanged() {
		importantEnchants = MiscUtil.massRegistryGet(enchantNames, ForgeRegistries.ENCHANTMENTS);
	}

	@SubscribeEvent
	public void onToolBreak(PlayerDestroyItemEvent event) {
		Player player = event.getEntity();
		ItemStack stack = event.getOriginal();
		Item item = stack.getItem();

		if(player != null && player.level != null && !player.level.isClientSide && !stack.isEmpty() && !(item instanceof ArmorItem) && (!unstackablesOnly || !stack.isStackable())) {
			int currSlot = player.getInventory().selected;
			if(event.getHand() == InteractionHand.OFF_HAND)
				currSlot = player.getInventory().getContainerSize() - 1;

			List<Enchantment> enchantmentsOnStack = getImportantEnchantments(stack);
			Predicate<ItemStack> itemPredicate = (other) -> other.getItem() == item;
			if(!stack.isDamageableItem())
				itemPredicate = itemPredicate.and((other) -> other.getDamageValue() == stack.getDamageValue());

			Predicate<ItemStack> enchantmentPredicate = (other) -> !(new ArrayList<>(enchantmentsOnStack)).retainAll(getImportantEnchantments(other));

			if(enableEnchantMatching && findReplacement(player, currSlot, itemPredicate.and(enchantmentPredicate)))
				return;

			if(findReplacement(player, currSlot, itemPredicate))
				return;

			if(enableLooseMatching) {
				Set<String> classes = getItemClasses(stack);

				if(!classes.isEmpty()) {
					Predicate<ItemStack> toolPredicate = (other) -> {
						Set<String> otherClasses = getItemClasses(other);
						return !otherClasses.isEmpty() && !otherClasses.retainAll(classes);
					};

					if(enableEnchantMatching && !enchantmentsOnStack.isEmpty() && findReplacement(player, currSlot, toolPredicate.and(enchantmentPredicate)))
						return;

					findReplacement(player, currSlot, toolPredicate);
				}
			}
		}
	}

	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent event) {
		if(event.phase == Phase.END && !event.player.level.isClientSide && replacements.containsKey(event.player)) {
			Stack<Pair<Integer, Integer>> replacementStack = replacements.get(event.player);
			synchronized(mutex) {
				while(!replacementStack.isEmpty()) {
					Pair<Integer, Integer> pair = replacementStack.pop();
					switchItems(event.player, pair.getLeft(), pair.getRight());
				}
			}
		}
	}

	private HashSet<String> getItemClasses(ItemStack stack) {
		Item item = stack.getItem();
		
		HashSet<String> classes = new HashSet<>();
		if(item instanceof BowItem)
			classes.add("bow");
		
		else if(item instanceof CrossbowItem)
			classes.add("crossbow");
		
		for(ToolAction action : ACTION_TO_CLASS.keySet()) {
			if(item.canPerformAction(stack, action))
				classes.add(ACTION_TO_CLASS.get(action));
		}
		
		GatherToolClassesEvent event = new GatherToolClassesEvent(stack, classes);
		MinecraftForge.EVENT_BUS.post(event);
		
		return classes;
	}

	private boolean findReplacement(Player player, int currSlot, Predicate<ItemStack> match) {
		synchronized(mutex) {
			for(int i = 0; i < player.getInventory().items.size(); i++) {
				if(i == currSlot)
					continue;

				ItemStack stackAt = player.getInventory().getItem(i);
				if(!stackAt.isEmpty() && match.test(stackAt)) {
					pushReplace(player, i, currSlot);
					return true;
				}
			}

			return false;
		}
	}

	private void pushReplace(Player player, int slot1, int slot2) {
		if(!replacements.containsKey(player))
			replacements.put(player, new Stack<>());
		replacements.get(player).push(Pair.of(slot1, slot2));
	}

	private void switchItems(Player player, int slot1, int slot2) {
		Inventory inventory = player.getInventory();
		int size = inventory.items.size();
		if(slot1 >= size || slot2 >= size)
			return;

		ItemStack stackAtSlot1 = inventory.getItem(slot1).copy();
		ItemStack stackAtSlot2 = inventory.getItem(slot2).copy();

		inventory.setItem(slot2, stackAtSlot1);
		inventory.setItem(slot1, stackAtSlot2);
	}

	private List<Enchantment> getImportantEnchantments(ItemStack stack) {
		List<Enchantment> enchantsOnStack = new ArrayList<>();
		for(Enchantment ench : importantEnchants)
			if(EnchantmentHelper.getItemEnchantmentLevel(ench, stack) > 0)
				enchantsOnStack.add(ench);

		return enchantsOnStack;
	}

	private static List<String> generateDefaultEnchantmentList() {
		Enchantment[] enchants = new Enchantment[] {
				Enchantments.SILK_TOUCH,
				Enchantments.BLOCK_FORTUNE,
				Enchantments.INFINITY_ARROWS,
				Enchantments.FISHING_LUCK,
				Enchantments.MOB_LOOTING
		};

		List<String> strings = new ArrayList<>();
		for(Enchantment e : enchants) 
			strings.add(Registry.ENCHANTMENT.getKey(e).toString());

		return strings;
	}

}
