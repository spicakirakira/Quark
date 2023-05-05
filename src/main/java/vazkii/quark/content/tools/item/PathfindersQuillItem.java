package vazkii.quark.content.tools.item;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.QuartPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.saveddata.maps.MapDecoration.Type;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.arl.interf.IItemColorProvider;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.quark.base.item.QuarkItem;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.content.tools.module.PathfinderMapsModule;
import vazkii.quark.content.tools.module.PathfinderMapsModule.TradeInfo;

public class PathfindersQuillItem extends QuarkItem implements IItemColorProvider {

	private static final Direction[] DIRECTIONS = new Direction[] { Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.NORTH };

	public static final String TAG_BIOME = "targetBiome";
	public static final String TAG_COLOR = "targetBiomeColor";
	public static final String TAG_UNDERGROUND = "targetBiomeUnderground";

	private static final String TAG_IS_SEARCHING = "isSearchingForBiome";
	private static final String TAG_SOURCE_X = "searchSourceX";
	private static final String TAG_SOURCE_Z = "searchSourceZ";
	private static final String TAG_POS_X = "searchPosX";
	private static final String TAG_POS_Z = "searchPosZ";
	private static final String TAG_POS_LEG = "searchPosLeg";
	private static final String TAG_POS_LEG_INDEX = "searchPosLegIndex";

	public PathfindersQuillItem(QuarkModule module) {
		super("pathfinders_quill", module, new Item.Properties().tab(CreativeModeTab.TAB_TOOLS).stacksTo(1));
	}

	public static ResourceLocation getTargetBiome(ItemStack stack) {
		String str = ItemNBTHelper.getString(stack, TAG_BIOME, "");
		if(str.isEmpty())
			return null;

		return new ResourceLocation(str);
	}

	public static int getOverlayColor(ItemStack stack) {
		return ItemNBTHelper.getInt(stack, TAG_COLOR, 0xFFFFFF);
	}

	public static ItemStack forBiome(String biome, int color) {
		ItemStack stack = new ItemStack(PathfinderMapsModule.pathfinders_quill);
		setBiome(stack, biome, color, false);
		return stack;
	}
	
	public static void setBiome(ItemStack stack, String biome, int color, boolean underground) {
		ItemNBTHelper.setString(stack, TAG_BIOME, biome);
		ItemNBTHelper.setInt(stack, TAG_COLOR, color);
		ItemNBTHelper.setBoolean(stack, TAG_UNDERGROUND, underground);
	}

	public static ItemStack getActiveQuill(Player player) {
		for(ItemStack stack : player.getInventory().items)
			if(stack.getItem() == PathfinderMapsModule.pathfinders_quill) {
				boolean searching = ItemNBTHelper.getBoolean(stack, TAG_IS_SEARCHING, false);

				if(searching)
					return stack;
			}

		return null;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		ResourceLocation target = getTargetBiome(stack);
		if(target == null)
			return InteractionResultHolder.pass(stack);

		ItemStack active = getActiveQuill(player);
		if(active != null)
			return InteractionResultHolder.fail(stack);

		ItemNBTHelper.setBoolean(stack, TAG_IS_SEARCHING, true);
		ItemNBTHelper.setInt(stack, TAG_SOURCE_X, player.getBlockX());
		ItemNBTHelper.setInt(stack, TAG_SOURCE_Z, player.getBlockZ());
		return InteractionResultHolder.success(stack);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean held) {
		if(!level.isClientSide 
				&& level instanceof ServerLevel sl
				&& ItemNBTHelper.getBoolean(stack, TAG_IS_SEARCHING, false) 
				&& entity instanceof Player player 
				&& getActiveQuill(player) == stack) {

			int attempts = 12;
			ItemStack runningStack = stack;
			for(int i = 0; i < attempts; i++) {
				runningStack = search(runningStack, sl, player, slot);
				
				if(runningStack != stack) {
					player.getInventory().setItem(slot, runningStack);
					return;
				}
			}
		}
	}

	private ItemStack search(ItemStack stack, ServerLevel level, Player player, int slot) {
		final int height = 64;
		
		ResourceLocation searchKey = getTargetBiome(stack);
		BlockPos nextPos = nextPos(stack);
		if(searchKey == null || nextPos == null)
			return ItemStack.EMPTY;
		
		int[] searchedHeights = Mth.outFromOrigin(player.getBlockY(), level.getMinBuildHeight() + 1, level.getMaxBuildHeight(), height).toArray();

		int testX = nextPos.getX();
		int testZ = nextPos.getZ();
		int quartX = QuartPos.fromBlock(testX);
		int quartZ = QuartPos.fromBlock(testZ);

		for(int testY : searchedHeights) {
			int quartY = QuartPos.fromBlock(testY);

			ServerChunkCache cache = level.getChunkSource();
			BiomeSource source = cache.getGenerator().getBiomeSource();
			Climate.Sampler sampler = cache.randomState().sampler();

			Holder<Biome> holder = source.getNoiseBiome(quartX, quartY, quartZ, sampler);

			if(holder.is(searchKey)) {
				BlockPos mapPos = new BlockPos(testX, testY, testZ);
				System.out.println("FOUND AT  " + mapPos);
				ItemStack map = createMap(level, mapPos, searchKey, getOverlayColor(stack));
				return map;
			}
		}
		
		return stack;
	}

	private static BlockPos nextPos(ItemStack stack) {
		final int step = 32;
		
		int sourceX = ItemNBTHelper.getInt(stack, TAG_SOURCE_X, 0);
		int sourceZ = ItemNBTHelper.getInt(stack, TAG_SOURCE_Z, 0);
		
		int x = ItemNBTHelper.getInt(stack, TAG_POS_X, 0);
		int z = ItemNBTHelper.getInt(stack, TAG_POS_Z, 0);
		int leg = ItemNBTHelper.getInt(stack, TAG_POS_LEG, -1);
		int legIndex = ItemNBTHelper.getInt(stack, TAG_POS_LEG_INDEX, 0);

		BlockPos cursor = new BlockPos(x, 0, z).relative(DIRECTIONS[(leg + 4) % 4]);

		int newX = cursor.getX();
		int newZ = cursor.getZ();

		int legSize = leg / 2 + 1;
		int maxLegs = 4 * Math.floorDiv(PathfinderMapsModule.searchRadius, step);
		
		if (legIndex >= legSize) {
			if(leg > maxLegs)
				return null;

			leg++;
			legIndex = 0;
		}

		legIndex++;

		ItemNBTHelper.setInt(stack, TAG_POS_X, newX);
		ItemNBTHelper.setInt(stack, TAG_POS_Z, newZ);
		ItemNBTHelper.setInt(stack, TAG_POS_LEG, leg);
		ItemNBTHelper.setInt(stack, TAG_POS_LEG_INDEX, legIndex);

		int retX = sourceX + newX * step;
		int retZ = sourceZ + newZ * step;
		
		return new BlockPos(retX, 0, retZ);
	}

	public static ItemStack createMap(Level world, BlockPos biomePos, ResourceLocation biome, int color) {
		if(!(world instanceof ServerLevel serverLevel))
			return ItemStack.EMPTY;

		Component biomeComponent = Component.translatable("biome." + biome.getNamespace() + "." + biome.getPath());

		ItemStack stack = MapItem.create(world, biomePos.getX(), biomePos.getZ(), (byte) 2, true, true);

		MapItem.renderBiomePreviewMap(serverLevel, stack);
		MapItemSavedData.addTargetDecoration(stack, biomePos, "+", Type.RED_X);
		stack.setHoverName(Component.translatable("item.quark.biome_map", biomeComponent));

		stack.getOrCreateTagElement("display").putInt("MapColor", color);
		ItemNBTHelper.setBoolean(stack, PathfinderMapsModule.TAG_IS_PATHFINDER, true);

		return stack;
	}

	@Override
	public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
		if((isEnabled() && allowedIn(group)) || group == CreativeModeTab.TAB_SEARCH) {
			for(TradeInfo trade : PathfinderMapsModule.tradeList)
				items.add(forBiome(trade.biome.toString(), trade.color));
		}
	}

	@Override
	public void appendHoverText(ItemStack stack, Level level, List<Component> comps, TooltipFlag flags) {
		ResourceLocation biome = getTargetBiome(stack); 
		if(biome != null)
			comps.add(Component.translatable("biome." + biome.getNamespace() + "." + biome.getPath()).withStyle(ChatFormatting.GRAY));
		else comps.add(Component.translatable("item.quark.pathfinders_quill_unset").withStyle(ChatFormatting.GRAY));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public ItemColor getItemColor() {
		return (stack, id) -> id == 0 ? 0xFFFFFF : getOverlayColor(stack);
	}

}
