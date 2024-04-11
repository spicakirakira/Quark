package org.violetmoon.quark.content.experimental.module;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.violetmoon.quark.base.Quark;
import org.violetmoon.quark.content.building.module.HollowLogsModule;
import org.violetmoon.zeta.config.Config;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.bus.PlayEvent;
import org.violetmoon.zeta.event.load.ZCommonSetup;
import org.violetmoon.zeta.event.load.ZConfigChanged;
import org.violetmoon.zeta.event.play.entity.player.ZRightClickBlock;
import org.violetmoon.zeta.module.ZetaLoadModule;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.util.MiscUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;

@ZetaLoadModule(category = "experimental", enabledByDefault = false, description = "For testing purposes, do not enable yet")
public class FallenLogsModule extends ZetaModule {

	@Config(description = "Requires the Hollow Logs module to be enabled too")
	public static boolean useHollowLogs = true;
	
	@Config
	public static int rarity = 3;
	
	@Config
	public static int sparseBiomeRarity = 8;
	
	@Config(description = "Tags that define which biomes can have which wood types")
	public static List<String> biomeTags = Arrays.asList(new String[] {
			"quark:has_fallen_acacia=minecraft:acacia_log",
			"quark:has_fallen_birch=minecraft:birch_log",
			"quark:has_fallen_cherry=minecraft:cherry_log",
			"quark:has_fallen_dark_oak=minecraft:dark_oak_log",
			"quark:has_fallen_jungle=minecraft:jungle_log",
			"quark:has_fallen_mangrove=minecraft:mangrove_log",
			"quark:has_fallen_oak=minecraft:oak_log",
			"quark:has_fallen_spruce=minecraft:spruce_log"
	});

	public static Map<TagKey<Biome>, Block> blocksPerTag = new HashMap<>();
	
	public static TagKey<Biome> reducedLogsTag;

	@LoadEvent
	public final void setup(ZCommonSetup event) {
		reducedLogsTag = TagKey.create(Registries.BIOME, new ResourceLocation(Quark.MOD_ID, "has_lower_fallen_tree_density"));
	}
	
	@LoadEvent
	public final void configChanged(ZConfigChanged event) {
		blocksPerTag.clear();
		for(String s : biomeTags) {
			String[] toks = s.split("=");
			
			String k = toks[0];
			String v = toks[1];
			
			TagKey<Biome> tag = TagKey.create(Registries.BIOME, new ResourceLocation(k));
			Block block = BuiltInRegistries.BLOCK.get(new ResourceLocation(v));
			
			if(block == null)
				throw new IllegalArgumentException("Block " + v + " doesn't exist");
			blocksPerTag.put(tag, block);
		}
	}
	
	@PlayEvent
	public void onUseOnBlock(ZRightClickBlock event) {
		ItemStack stack = event.getItemStack();

		if(stack.is(Items.QUARTZ) && !event.getLevel().isClientSide && event.getHand() == InteractionHand.MAIN_HAND) {
			placeFallenLogAt(event.getLevel(), event.getPos().above());
		}
	}

	private static void placeFallenLogAt(LevelAccessor level, BlockPos pos) {
		placeFallenLogAt(level, pos, getLogBLockForPos(level, pos));
	}

	private static void placeFallenLogAt(LevelAccessor level, BlockPos pos, Block logBlock) {
		if(logBlock == Blocks.AIR)
			return;
		
		final int attempts = 5;
		
		BlockState state = logBlock.defaultBlockState();
		RandomSource rand = level.getRandom();

		for(int attempt = 0; attempt < attempts; attempt++) {
			int dirOrd = rand.nextInt(MiscUtil.HORIZONTALS.length);
			Direction dir = MiscUtil.HORIZONTALS[dirOrd];
			state = state.setValue(RotatedPillarBlock.AXIS, dir.getAxis());

			int len = 3 + rand.nextInt(2);
			
			boolean errored = false;
			
			for(int i = 0; i < len; i++) {
				BlockPos testPos = pos.relative(dir, i);
				BlockState testState = level.getBlockState(testPos);

				if(!testState.isAir() && !testState.canBeReplaced()) {
					errored = true;
					break;
				}

				BlockPos belowPos = testPos.below();
				BlockState belowState = level.getBlockState(belowPos);

				if(belowState.isAir()) {
					errored = true;
					break;
				}
			}
			
			if(errored)
				continue;

			for(int i = 0; i < len; i++) {
				BlockPos placePos = pos.relative(dir, i); 
				level.setBlock(placePos, state, 3);

				if(rand.nextInt(10) < 7) {
					BlockPos abovePos = placePos.above();
					BlockState aboveState = level.getBlockState(abovePos);
					if(aboveState.isAir()) {
						level.setBlock(abovePos, Blocks.MOSS_CARPET.defaultBlockState(), 3);
					}
				}

				final Direction[][] sideDirections = {
						{Direction.EAST, Direction.WEST},
						{Direction.EAST, Direction.WEST},
						{Direction.NORTH, Direction.SOUTH},
						{Direction.NORTH, Direction.SOUTH}
				};

				for(int j = 0; j < 2; j++)
					if(rand.nextInt(5) < 3) {
						Direction side = sideDirections[dirOrd][j];
						BlockPos sidePos = placePos.relative(side);
						placeDecorIfPossible(level, rand, side, sidePos);
					}
				
				if(rand.nextInt(10) < 4)
					placeDecorIfPossible(level, rand, dir, pos.relative(dir.getOpposite()));
				if(rand.nextInt(10) < 4)
					placeDecorIfPossible(level, rand, dir.getOpposite(), pos.relative(dir, len));
			}
			
			return;
		}
	}
	
	private static void placeDecorIfPossible(LevelAccessor level, RandomSource rand, Direction side, BlockPos sidePos) {
		BlockState sideState = level.getBlockState(sidePos);
		if(sideState.isAir()) {
			BlockState placeState = switch(rand.nextInt(3)) {
				case 0 -> Blocks.MOSS_CARPET.defaultBlockState();
				case 1 -> Blocks.VINE.defaultBlockState().setValue(VineBlock.getPropertyForFace(side.getOpposite()), true);
				default -> Blocks.FERN.defaultBlockState();
			};
			
			if(placeState.canSurvive(level, sidePos))
				level.setBlock(sidePos, placeState, 3);
		}
	}

	private static Block getLogBLockForPos(LevelAccessor level, BlockPos pos) {
		Block base = getBaseLogBlockForPos(level, pos);

		if(useHollowLogs && HollowLogsModule.staticEnabled) {
			Block hollow = HollowLogsModule.logMap.get(base);
			if(hollow != null)
				return hollow;
		}

		return base;
	}

	private static Block getBaseLogBlockForPos(LevelAccessor level, BlockPos pos) {
		Holder<Biome> biome = level.getBiome(pos);
		List<Block> matched = new ArrayList<>();
		
		for(TagKey<Biome> tag : blocksPerTag.keySet())
			if(biome.is(tag))
				matched.add(blocksPerTag.get(tag));
		
		if(matched.size() == 0)
			return Blocks.AIR;
		
		return matched.get(level.getRandom().nextInt(matched.size()));
	}

}
