package vazkii.quark.mixin;

import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Items.class)
public class ItemsMixin {

	@Inject(method = "ifPart2", at = @At("HEAD"), cancellable = true)
	private static <T> void overrideStackedOnOther(T val, CallbackInfoReturnable<Optional<T>> callbackInfoReturnable) {
		callbackInfoReturnable.setReturnValue(Optional.of(val));
	}

}
