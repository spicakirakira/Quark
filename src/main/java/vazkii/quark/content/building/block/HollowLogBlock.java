package vazkii.quark.content.building.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.base.handler.RenderLayerHandler;
import vazkii.quark.base.handler.RenderLayerHandler.RenderTypeSkeleton;
import vazkii.quark.base.module.QuarkModule;

public class HollowLogBlock extends HollowPillarBlock {

    private final boolean flammable;

    public HollowLogBlock(Block sourceLog, QuarkModule module, boolean flammable) {
        super(IQuarkBlock.inherit(sourceLog, "hollow_%s"), module, CreativeModeTab.TAB_DECORATIONS,
                Properties.copy(sourceLog)
                        .noOcclusion()
                        .isSuffocating((s, g, p) -> false));

        this.flammable = flammable;
        RenderLayerHandler.setRenderType(this, RenderTypeSkeleton.CUTOUT_MIPPED);
    }

    @Override
    public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return flammable;
    }
}

