package vazkii.quark.base.block;

import java.util.function.BooleanSupplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.IPlantable;
import vazkii.quark.base.module.QuarkModule;

// Wrapper to allow vanilla blocks to be treated as quark blocks contextualized under a module
public class QuarkBlockWrapper implements IQuarkBlock {

	private final Block parent;
	private final QuarkModule module;
	
	private BooleanSupplier condition;
	
	public QuarkBlockWrapper(Block parent, QuarkModule module) {
		this.parent = parent;
		this.module = module;
	}
	
	@Override
	public Block getBlock() {
		return parent;
	}

	@Override
	public QuarkModule getModule() {
		return module;
	}

	@Override
	public QuarkBlockWrapper setCondition(BooleanSupplier condition) {
		this.condition = condition;
		return this;
	}

	@Override
	public boolean doesConditionApply() {
		return condition == null || condition.getAsBoolean();
	}

	@Override
	public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, IPlantable plantable) {
		return false;
	}

}
