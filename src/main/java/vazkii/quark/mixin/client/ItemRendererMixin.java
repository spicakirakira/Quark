package vazkii.quark.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.quark.content.management.module.ItemSharingModule;
import vazkii.quark.content.tools.module.ColorRunesModule;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {

	@Inject(method = "render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/renderer/block/model/ItemTransforms$TransformType;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V", at = @At("HEAD"))
	private void setColorRuneTargetStack(ItemStack itemStackIn, ItemTransforms.TransformType transformTypeIn, boolean leftHand, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn, BakedModel modelIn, CallbackInfo callbackInfo) {
		ColorRunesModule.setTargetStack(itemStackIn);
	}

	@Redirect(method = "getArmorFoilBuffer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;armorGlint()Lnet/minecraft/client/renderer/RenderType;"))
	private static RenderType getArmorGlint() {
		return ColorRunesModule.getArmorGlint();
	}

	@Redirect(method = "getArmorFoilBuffer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;armorEntityGlint()Lnet/minecraft/client/renderer/RenderType;"))
	private static RenderType getArmorEntityGlint() {
		return ColorRunesModule.getArmorEntityGlint();
	}

	@Redirect(method = "getFoilBuffer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;glintTranslucent()Lnet/minecraft/client/renderer/RenderType;"))
	private static RenderType getGlintTranslucent() {
		return ColorRunesModule.getGlintTranslucent();
	}

	@Redirect(method = "getFoilBuffer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;glint()Lnet/minecraft/client/renderer/RenderType;"))
	private static RenderType getGlint() {
		return ColorRunesModule.getGlint();
	}

	@Redirect(method = "getFoilBuffer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;entityGlint()Lnet/minecraft/client/renderer/RenderType;"))
	private static RenderType getEntityGlint() {
		return ColorRunesModule.getEntityGlint();
	}

	@Redirect(method = "getFoilBufferDirect", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;glintDirect()Lnet/minecraft/client/renderer/RenderType;"))
	private static RenderType getGlintDirect() {
		return ColorRunesModule.getGlintDirect();
	}

	@Redirect(method = "getFoilBufferDirect", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;entityGlintDirect()Lnet/minecraft/client/renderer/RenderType;"))
	private static RenderType getEntityGlintDirect() {
		return ColorRunesModule.getEntityGlintDirect();
	}

	@ModifyConstant(method = "renderQuadList", constant = @Constant(floatValue = 1F))
	public float renderQuads(float constant) {
		return ItemSharingModule.alphaValue * constant;
	}
}
