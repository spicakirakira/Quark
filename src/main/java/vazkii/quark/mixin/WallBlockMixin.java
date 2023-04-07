package vazkii.quark.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import vazkii.quark.content.building.module.VerticalSlabsModule;

@Mixin(WallBlock.class)
public class WallBlockMixin {

	@Inject(method = "connectsTo", at = @At("RETURN"), cancellable = true)
	private void connectsTo(BlockState state, boolean sturdy, Direction dir, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
		callbackInfoReturnable.setReturnValue(VerticalSlabsModule.shouldWallConnect(state, dir, callbackInfoReturnable.getReturnValueZ()));
	}
	
}
