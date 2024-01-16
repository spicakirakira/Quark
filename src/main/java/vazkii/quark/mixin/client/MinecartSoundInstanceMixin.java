package vazkii.quark.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.resources.sounds.MinecartSoundInstance;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import vazkii.quark.content.client.module.WoolShutsUpMinecartsModule;

@Mixin(MinecartSoundInstance.class)
public class MinecartSoundInstanceMixin {

	@Shadow
	@Final
	private AbstractMinecart minecart;

	@Inject(method = "canPlaySound", at = @At("HEAD"), cancellable = true)
	public void canPlay(CallbackInfoReturnable<Boolean> ci) {
		if(!WoolShutsUpMinecartsModule.canPlay(minecart)) {
			ci.setReturnValue(false);
			ci.cancel();
		}
	}


}
