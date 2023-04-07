package vazkii.quark.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockState;
import vazkii.quark.content.building.module.VerticalSlabsModule;

@Mixin(IronBarsBlock.class)
public class IronBarsBlockMixin {

	@Inject(method = "getStateForPlacement", at = @At("RETURN"), cancellable = true)
	private void connectsTo(BlockPlaceContext context, CallbackInfoReturnable<BlockState> callbackInfoReturnable) {
		callbackInfoReturnable.setReturnValue(VerticalSlabsModule.messWithPaneState(context.getLevel(), context.getClickedPos(), callbackInfoReturnable.getReturnValue()));
	}
	
	@Inject(method = "updateShape", at = @At("RETURN"), cancellable = true)
	private void updateShape(BlockState state, Direction dir, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos, CallbackInfoReturnable<BlockState> callbackInfoReturnable) {
		callbackInfoReturnable.setReturnValue(VerticalSlabsModule.messWithPaneState(level, pos, callbackInfoReturnable.getReturnValue()));	
	}
	
	
}
