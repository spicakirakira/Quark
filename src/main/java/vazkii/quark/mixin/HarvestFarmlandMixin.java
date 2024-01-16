package vazkii.quark.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.behavior.HarvestFarmland;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import vazkii.quark.content.tweaks.module.SimpleHarvestModule;

@Mixin(HarvestFarmland.class)
public class HarvestFarmlandMixin {

	@WrapOperation(method = "tick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/npc/Villager;J)V",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;destroyBlock(Lnet/minecraft/core/BlockPos;ZLnet/minecraft/world/entity/Entity;)Z"))
	private boolean harvestAndReplant(ServerLevel instance, BlockPos pos, boolean b, Entity entity, Operation<Boolean> original) {
		if (!SimpleHarvestModule.staticEnabled && SimpleHarvestModule.villagersUseSimpleHarvest) {
			BlockState state = instance.getBlockState(pos);
			SimpleHarvestModule.harvestAndReplant(instance, pos, state, entity, ItemStack.EMPTY);
			if (state.equals(instance.getBlockState(pos)))
				return original.call(instance, pos, b, entity);
			return true;
		}
		return original.call(instance, pos, b, entity);
	}
}
