package vazkii.quark.mixin.client;

import net.minecraft.client.renderer.entity.ParrotRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Parrot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.quark.content.tools.module.ParrotEggsModule;

@Mixin(ParrotRenderer.class)
public class ParrotRendererMixin {
	@Inject(method = "getTextureLocation(Lnet/minecraft/world/entity/animal/Parrot;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true)
	private void overrideTexture(Parrot parrot, CallbackInfoReturnable<ResourceLocation> cir) {
		ResourceLocation loc = ParrotEggsModule.getTextureForParrot(parrot);
		if (loc != null)
			cir.setReturnValue(loc);
	}
}
