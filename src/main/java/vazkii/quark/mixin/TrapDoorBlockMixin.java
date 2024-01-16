package vazkii.quark.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import vazkii.quark.content.building.module.VariantLaddersModule;

@Mixin(TrapDoorBlock.class)
public class TrapDoorBlockMixin {

	@ModifyReturnValue(method = "isLadder", at = @At("RETURN"), remap = false)
	private boolean isTrapdoorLadder(boolean prev, BlockState state, LevelReader world, BlockPos pos, LivingEntity entity) {
		return VariantLaddersModule.isTrapdoorLadder(prev, world, pos);
	}
}
