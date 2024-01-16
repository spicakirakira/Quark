package vazkii.quark.mixin.client.variants;

import net.minecraft.client.renderer.entity.BeeRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Bee;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.quark.content.client.module.VariantAnimalTexturesModule;

@Mixin(BeeRenderer.class)
public class BeeRendererMixin {
	@Inject(method = "getTextureLocation(Lnet/minecraft/world/entity/animal/Bee;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true)
	private void overrideTexture(Bee bee, CallbackInfoReturnable<ResourceLocation> cir) {
		ResourceLocation loc = VariantAnimalTexturesModule.getBeeTexture(bee);
		if (loc != null)
			cir.setReturnValue(loc);
	}
}
