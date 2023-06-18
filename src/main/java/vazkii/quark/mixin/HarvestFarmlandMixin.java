package vazkii.quark.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.behavior.HarvestFarmland;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import vazkii.quark.content.tweaks.module.SimpleHarvestModule;

@Mixin(HarvestFarmland.class)
public class HarvestFarmlandMixin {

	@Redirect(method = "tick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/npc/Villager;J)V",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;destroyBlock(Lnet/minecraft/core/BlockPos;ZLnet/minecraft/world/entity/Entity;)Z"))
	private boolean harvestAndReplant(ServerLevel instance, BlockPos pos, boolean b, Entity entity) {
		if (!SimpleHarvestModule.staticEnabled && SimpleHarvestModule.villagersUseSimpleHarvest) {
			BlockState state = instance.getBlockState(pos);
			SimpleHarvestModule.harvestAndReplant(instance, pos, state, entity, ItemStack.EMPTY);
			if (state.equals(instance.getBlockState(pos)))
				return instance.destroyBlock(pos, b, entity);
			return true;
		}
		return instance.destroyBlock(pos, b, entity);
	}
}
