package vazkii.quark.mixin;

import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;
import vazkii.quark.base.handler.QuarkPistonStructureResolver;
import vazkii.quark.content.automation.module.PistonsMoveTileEntitiesModule;
import vazkii.quark.content.experimental.module.GameNerfsModule;

import java.util.Map;

@Mixin(PistonBaseBlock.class)
public class PistonBaseBlockMixin {

	@Unique
	private BlockPos oldPos;

	@Unique
	private BlockState newState;

	@Unique
	private Map<BlockPos, BlockState> storedMap;

	@Redirect(method = "isPushable", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;hasBlockEntity()Z"))
	private static boolean isPushable(BlockState blockStateIn) {
		return PistonsMoveTileEntitiesModule.shouldMoveTE(blockStateIn.hasBlockEntity(), blockStateIn);
	}

	@Inject(method = "moveBlocks", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/piston/PistonStructureResolver;getToPush()Ljava/util/List;"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void moveBlocks(Level worldIn, BlockPos pos, Direction directionIn, boolean extending, CallbackInfoReturnable<Boolean> callbackInfoReturnable, BlockPos _pos, PistonStructureResolver pistonBlockStructureHelper) {
		PistonsMoveTileEntitiesModule.detachTileEntities(worldIn, pistonBlockStructureHelper, directionIn, extending);
	}

	@Redirect(method = {"checkIfExtend", "moveBlocks"}, at = @At(value = "NEW", target = "net/minecraft/world/level/block/piston/PistonStructureResolver"))
	private PistonStructureResolver transformStructureHelper(Level worldIn, BlockPos posIn, Direction pistonFacing, boolean extending) {
		return new QuarkPistonStructureResolver(new PistonStructureResolver(worldIn, posIn, pistonFacing, extending), worldIn, posIn, pistonFacing, extending);
	}

	@ModifyVariable(method = "moveBlocks", at = @At(value = "STORE", ordinal = 0), index = 15, ordinal = 2, slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addDestroyBlockEffect(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V"), to = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/piston/MovingPistonBlock;newMovingBlockEntity(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;ZZ)Lnet/minecraft/world/level/block/entity/BlockEntity;")))
	private BlockPos storeOldPos(BlockPos pos) {
		oldPos = pos;
		return pos;
	}

	@ModifyVariable(method = "moveBlocks", at = @At(value = "STORE", ordinal = 0), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/piston/PistonStructureResolver;resolve()Z"), to = @At(value = "INVOKE", target = "Lcom/google/common/collect/Lists;newArrayList()Ljava/util/ArrayList;", remap = false)))
	private Map<BlockPos, BlockState> storeMap(Map<BlockPos, BlockState> map) {
		storedMap = map;
		return map;
	}

	@Inject(method = "moveBlocks", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", ordinal = 2, shift = At.Shift.AFTER))
	private void modifyBlockstate(Level worldIn, BlockPos posIn, Direction pistonFacing, boolean extending, CallbackInfoReturnable<Boolean> cir) {
		if (GameNerfsModule.stopPistonPhysicsExploits()) {
			newState = worldIn.getBlockState(oldPos);
			storedMap.replace(oldPos, newState);
		}
	}

	@ModifyArg(method = "moveBlocks", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/piston/MovingPistonBlock;newMovingBlockEntity(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;ZZ)Lnet/minecraft/world/level/block/entity/BlockEntity;", ordinal = 0), index = 2)
	private BlockState modifyMovingBlockEntityState(BlockState state) {
		return GameNerfsModule.stopPistonPhysicsExploits() ? newState : state;
	}

	@Inject(method = "moveBlocks", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlockEntity(Lnet/minecraft/world/level/block/entity/BlockEntity;)V", ordinal = 0, shift = At.Shift.AFTER))
	private void setOldPosToAir(Level worldIn, BlockPos pos, Direction directionIn, boolean extending, CallbackInfoReturnable<Boolean> cir) {
		if (GameNerfsModule.stopPistonPhysicsExploits()) {
			worldIn.setBlock(oldPos, Blocks.AIR.defaultBlockState(), 2 | 4 | 16 | 1024); // paper impl comment: set air to prevent later physics updates from seeing this block
		}
	}
}
