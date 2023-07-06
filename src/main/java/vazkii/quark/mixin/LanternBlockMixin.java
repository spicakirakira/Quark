package vazkii.quark.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import vazkii.quark.content.building.module.WoodenPostsModule;

@Mixin(LanternBlock.class)
public class LanternBlockMixin {

	@ModifyReturnValue(method = "canSurvive", at = @At("RETURN"))
	private boolean canSurvive(boolean prev, BlockState state, LevelReader worldIn, BlockPos pos) {
		return WoodenPostsModule.canLanternConnect(state, worldIn, pos, prev);
	}

}
