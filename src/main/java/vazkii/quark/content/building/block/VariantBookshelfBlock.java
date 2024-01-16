package vazkii.quark.content.building.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.module.QuarkModule;

public class VariantBookshelfBlock extends QuarkBlock {

	private final boolean flammable;
	
	public VariantBookshelfBlock(String type, QuarkModule module, boolean flammable) {
		super(type + "_bookshelf", module, CreativeModeTab.TAB_BUILDING_BLOCKS, Block.Properties.copy(Blocks.BOOKSHELF));
		this.flammable = flammable;
	}
	
	@Override
	public boolean isFlammable(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return flammable;
	}
	
	@Override
	public float getEnchantPowerBonus(BlockState state, LevelReader world, BlockPos pos) {
		return 1;
	}
}
