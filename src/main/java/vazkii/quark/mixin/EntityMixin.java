package vazkii.quark.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.entity.Entity;
import vazkii.quark.content.tools.module.PickarangModule;

@Mixin(Entity.class)
public class EntityMixin {

	@Inject(method = "fireImmune", at = @At("RETURN"), cancellable = true)
	private void isImmuneToFire(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
		boolean original = callbackInfoReturnable.getReturnValue();
		boolean changed = PickarangModule.getIsFireResistant(original, (Entity) (Object) this);
		if(original != changed)
			callbackInfoReturnable.setReturnValue(changed);
	}

}
