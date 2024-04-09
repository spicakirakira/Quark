package org.violetmoon.quark.content.experimental.module;

import org.violetmoon.quark.content.building.module.HollowLogsModule;
import org.violetmoon.zeta.config.Config;
import org.violetmoon.zeta.event.bus.PlayEvent;
import org.violetmoon.zeta.event.play.entity.player.ZRightClickBlock;
import org.violetmoon.zeta.module.ZetaLoadModule;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.util.MiscUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;

@ZetaLoadModule(category = "experimental", enabledByDefault = false, description = "For testing purposes, do not enable yet")
public class FallenLogsModule extends ZetaModule {

	@Config(description = "Requires the Hollow Logs module to be enabled too")
	public static boolean useHollowLogs = true;

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
		BlockState state = logBlock.defaultBlockState();
		RandomSource rand = level.getRandom();

		int dirOrd = rand.nextInt(MiscUtil.HORIZONTALS.length);
		Direction dir = MiscUtil.HORIZONTALS[dirOrd];
		state = state.setValue(RotatedPillarBlock.AXIS, dir.getAxis());

		int len = 3 + rand.nextInt(2);

		for(int i = 0; i < len; i++) {
			BlockPos testPos = pos.relative(dir, i);
			BlockState testState = level.getBlockState(testPos);

			if(!testState.isAir() && !testState.canBeReplaced())
				return;

			BlockPos belowPos = testPos.below();
			BlockState belowState = level.getBlockState(belowPos);

			if(belowState.isAir())
				return;
		}

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
		return Blocks.OAK_LOG; // TODO pick based on biome tags
	}

}
