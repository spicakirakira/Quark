package vazkii.quark.mixin;

import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.quark.content.experimental.module.VillagerRerollingReworkModule;

@Mixin(Villager.class)
public class VillagerMixin {

	@Inject(method = "resetNumberOfRestocks", at = @At("TAIL"))
	public void resetRestocks(CallbackInfo ci) {
		Villager villager = (Villager) (Object) this;
		VillagerRerollingReworkModule.attemptToReroll(villager);
	}
}
