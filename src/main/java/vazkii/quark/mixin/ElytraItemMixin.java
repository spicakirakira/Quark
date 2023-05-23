package vazkii.quark.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import vazkii.quark.content.experimental.module.GameNerfsModule;

@Mixin(ElytraItem.class)
public class ElytraItemMixin {
	
	@Inject(method = "canElytraFly", at = @At("RETURN"), cancellable = true, remap = false)
	private void canApply(ItemStack stack, LivingEntity living, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
		boolean ret = GameNerfsModule.canEntityUseElytra(living, callbackInfoReturnable.getReturnValueZ());
		callbackInfoReturnable.setReturnValue(ret);
	}
	
}
