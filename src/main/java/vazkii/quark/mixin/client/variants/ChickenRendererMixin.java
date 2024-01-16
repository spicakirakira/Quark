package vazkii.quark.mixin.client.variants;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.model.ChickenModel;
import net.minecraft.client.renderer.entity.ChickenRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Chicken;
import vazkii.quark.content.client.module.VariantAnimalTexturesModule;
import vazkii.quark.content.tweaks.module.GrabChickensModule;

@Mixin(ChickenRenderer.class)
public class ChickenRendererMixin {
	
	@Inject(method = "getTextureLocation(Lnet/minecraft/world/entity/animal/Chicken;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true)
	private void overrideTexture(Chicken chicken, CallbackInfoReturnable<ResourceLocation> cir) {
		ChickenRenderer render = (ChickenRenderer) ((Object) this);
		ChickenModel<Chicken> model = render.getModel();
		GrabChickensModule.setRenderChickenFeetStatus(chicken, model);
		
		ResourceLocation loc = VariantAnimalTexturesModule.getChickenTexture(chicken);
		if (loc != null)
			cir.setReturnValue(loc);
	}
}
