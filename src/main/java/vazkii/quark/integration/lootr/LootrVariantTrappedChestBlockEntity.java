package vazkii.quark.integration.lootr;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

/**
 * @author WireSegal
 * Created at 11:34 AM on 7/3/23.
 */
public class LootrVariantTrappedChestBlockEntity extends LootrVariantChestBlockEntity {

	protected LootrVariantTrappedChestBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public LootrVariantTrappedChestBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
		this(ILootrIntegration.INSTANCE.trappedChestTE(), pWorldPosition, pBlockState);
	}

	@Override
	protected void signalOpenCount(@Nonnull Level world, @Nonnull BlockPos pos, @Nonnull BlockState state, int prevOpenCount, int openCount) {
		super.signalOpenCount(world, pos, state, prevOpenCount, openCount);
		if (prevOpenCount != openCount) {
			Block block = state.getBlock();
			world.updateNeighborsAt(pos, block);
			world.updateNeighborsAt(pos.below(), block);
		}
	}
}
