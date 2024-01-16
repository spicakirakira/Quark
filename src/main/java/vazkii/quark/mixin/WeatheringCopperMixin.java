package vazkii.quark.mixin;

import net.minecraft.world.level.block.WeatheringCopper;
import org.spongepowered.asm.mixin.Mixin;
import vazkii.quark.mixinsupport.delegates.WeatheringCopperDelegate;
import vazkii.quark.mixinsupport.DelegateInterfaceMixin;
import vazkii.quark.mixinsupport.DelegateReturnValueModifier;

@Mixin(WeatheringCopper.class)
@DelegateInterfaceMixin(delegate = WeatheringCopperDelegate.class, methods = {
	@DelegateReturnValueModifier(target = "getPrevious(Lnet/minecraft/world/level/block/state/BlockState;)Ljava/util/Optional;",
		delegate = "customWeatheringPrevious", desc = "(Ljava/util/Optional;Lnet/minecraft/world/level/block/state/BlockState;)Ljava/util/Optional;"),
	@DelegateReturnValueModifier(target = "getFirst(Lnet/minecraft/world/level/block/state/BlockState;)Ljnet/minecraft/world/level/block/state/BlockState;",
		delegate = "customWeatheringFirst", desc = "(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/level/block/state/BlockState;")
})
public interface WeatheringCopperMixin {
	// Delegated. Only valid because WeatheringCopper members are not refmapped.
}
