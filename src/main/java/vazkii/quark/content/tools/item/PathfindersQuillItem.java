package vazkii.quark.content.tools.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.QuartPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.arl.interf.IItemColorProvider;
import vazkii.arl.util.ClientTicker;
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

	protected static final String TAG_IS_SEARCHING = "isSearchingForBiome";
	protected static final String TAG_SOURCE_X = "searchSourceX";
	protected static final String TAG_SOURCE_Z = "searchSourceZ";
	protected static final String TAG_POS_X = "searchPosX";
	protected static final String TAG_POS_Z = "searchPosZ";
	protected static final String TAG_POS_LEG = "searchPosLeg";
	protected static final String TAG_POS_LEG_INDEX = "searchPosLegIndex";

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

	public static @Nullable ItemStack getActiveQuill(Player player) {
		for(ItemStack stack : player.getInventory().items)
			if(stack.getItem() instanceof PathfindersQuillItem) {
				boolean searching = ItemNBTHelper.getBoolean(stack, TAG_IS_SEARCHING, false);

				if(searching)
					return stack;
			}

		return null;
	}
	
	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return slotChanged || (oldStack.getItem() != newStack.getItem());
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (!isNBTValid(stack)) return InteractionResultHolder.pass(stack);

		ItemStack active = getActiveQuill(player);
		if(active != null) {
			player.displayClientMessage(Component.translatable("quark.misc.only_one_quill"), true);
			return InteractionResultHolder.fail(stack);
		}
		
		Vec3 pos = player.getPosition(1F);
		level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS, 0.5F, 1F);

		ItemNBTHelper.setBoolean(stack, TAG_IS_SEARCHING, true);
		ItemNBTHelper.setInt(stack, TAG_SOURCE_X, player.getBlockX());
		ItemNBTHelper.setInt(stack, TAG_SOURCE_Z, player.getBlockZ());
		return InteractionResultHolder.success(stack);
	}

	private static boolean isNBTValid(ItemStack stack) {
		return getTargetBiome(stack) != null;
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean held) {
		if(!level.isClientSide 
				&& level instanceof ServerLevel sl
				&& ItemNBTHelper.getBoolean(stack, TAG_IS_SEARCHING, false) 
				&& entity instanceof Player player 
				&& getActiveQuill(player) == stack) {

			ItemStack runningStack = stack;
			for(int i = 0; i < PathfinderMapsModule.pathfindersQuillSpeed; i++) {
				runningStack = search(runningStack, sl, player, slot);
				
				if(runningStack != stack) {
					String msg = "quark.misc." + (runningStack.isEmpty() ? "quill_failed" : "quill_finished");
					player.displayClientMessage(Component.translatable(msg), true);
					
					Vec3 pos = player.getPosition(1F);
					level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.NOTE_BLOCK_CHIME, SoundSource.PLAYERS, 0.5F, 1F);
					
					player.getInventory().setItem(slot, runningStack);
					return;
				}
			}
		}
	}
	
	protected ItemStack search(ItemStack stack, ServerLevel level, Player player, int slot) {
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
				ItemStack map = createMap(level, mapPos, searchKey, getOverlayColor(stack));
				return map;
			}
		}
		
		return stack;
	}

	protected static BlockPos nextPos(ItemStack stack) {
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

	@OnlyIn(Dist.CLIENT)
	public static MutableComponent getSearchingComponent() {
		MutableComponent comp = Component.translatable("quark.misc.quill_searching");
		
		int dots = ((ClientTicker.ticksInGame / 10) % 4);
		for(int i = 0; i < dots; i++)
			comp.append(".");
		
		return comp;
	}
	
	@Override
	public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
		if((isEnabled() && allowedIn(group)) || group == CreativeModeTab.TAB_SEARCH) {
			for(TradeInfo trade : PathfinderMapsModule.tradeList)
				items.add(forBiome(trade.biome.toString(), trade.color));
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, Level level, List<Component> comps, TooltipFlag flags) {
		ResourceLocation biome = getTargetBiome(stack); 
		if(biome != null) {
			if(ItemNBTHelper.getBoolean(stack, TAG_IS_SEARCHING, false))
				comps.add(getSearchingComponent().withStyle(ChatFormatting.BLUE));

			comps.add(Component.translatable("biome." + biome.getNamespace() + "." + biome.getPath()).withStyle(ChatFormatting.GRAY));
		} else comps.add(Component.translatable("quark.misc.quill_blank").withStyle(ChatFormatting.GRAY));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public ItemColor getItemColor() {
		return (stack, id) -> id == 0 ? 0xFFFFFF : getOverlayColor(stack);
	}

}
