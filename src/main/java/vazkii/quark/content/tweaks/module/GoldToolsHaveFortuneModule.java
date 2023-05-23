package vazkii.quark.content.tweaks.module;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.config.Config.Max;
import vazkii.quark.base.module.config.Config.Min;

@LoadModule(category = ModuleCategory.TWEAKS, hasSubscriptions = true)
public class GoldToolsHaveFortuneModule extends QuarkModule {

	private static final Tier[] TIERS = new Tier[] {
			Tiers.WOOD, Tiers.STONE, Tiers.IRON, Tiers.DIAMOND, Tiers.NETHERITE
	};

	@Config
	@Min(0)
	public static int fortuneLevel = 2;

	@Config
	@Min(0)
	@Max(4)
	public static int harvestLevel = 2;
	
	@Config public static boolean displayFortuneInTooltip = true;
	@Config public static boolean italicTooltip = true;

	private static boolean staticEnabled;

	@Override
	public void configChanged() {
		staticEnabled = enabled;
	}
	
	@SubscribeEvent
	public void onLootingCheck(LootingLevelEvent event) {
		DamageSource source = event.getDamageSource();
		if(source != null && source.getEntity() != null) {
			Entity entity = source.getEntity();
			if(entity instanceof LivingEntity le) {
				ItemStack stack = le.getMainHandItem();
				
				if(stack.getItem() instanceof SwordItem) {
					int level = event.getLootingLevel();
					int target = getEffectiveLevel(stack, level);
					
					if(target > level)
						event.setLootingLevel(target);
				}
			}
		}
	}
	
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void onTooltip(ItemTooltipEvent event) {
		if(!displayFortuneInTooltip)
			return;
		
		ItemStack stack = event.getItemStack();
		int level = getEffectiveLevel(stack, 0);
		if(level == fortuneLevel) {
			Enchantment enchant = stack.getItem() instanceof SwordItem ? Enchantments.MOB_LOOTING : Enchantments.BLOCK_FORTUNE;
			int enchantLevel = EnchantmentHelper.getItemEnchantmentLevel(enchant, stack);
			if(enchantLevel < level) {
				Component comp = enchant.getFullname(level);
				if(italicTooltip)
					comp = comp.copy().withStyle(ChatFormatting.ITALIC);
				
				event.getToolTip().add(comp);
			}
		}
	}
	
	private static int getEffectiveLevel(ItemStack stack, int prevLvl) {
		if(stack.getItem() instanceof TieredItem ti) {
			Tier tier = ti.getTier();
			
			if(tier == Tiers.GOLD)
				return Math.max(prevLvl, fortuneLevel);
		}
		
		return prevLvl;
	}

	public static int getFortuneLevel(Enchantment enchant, ItemStack stack, int prev) {
		if(!staticEnabled || prev >= fortuneLevel || enchant != Enchantments.BLOCK_FORTUNE || !(stack.getItem() instanceof DiggerItem))
			return prev;

		return getEffectiveLevel(stack, prev);
	}
	
	public static Tier getEffectiveTier(Item item, Tier realTier) {
		if(!staticEnabled || (realTier != Tiers.GOLD))
			return realTier;

		return TIERS[harvestLevel];
	}

}
