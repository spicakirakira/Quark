package vazkii.quark.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.quark.content.management.module.ItemSharingModule;
import vazkii.quark.content.tools.module.ColorRunesModule;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {

	@Inject(method = "render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/renderer/block/model/ItemTransforms$TransformType;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V", at = @At("HEAD"))
	private void setColorRuneTargetStack(ItemStack itemStackIn, ItemTransforms.TransformType transformTypeIn, boolean leftHand, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn, BakedModel modelIn, CallbackInfo callbackInfo) {
		ColorRunesModule.setTargetStack(itemStackIn);
	}

	@ModifyExpressionValue(method = "getArmorFoilBuffer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;armorGlint()Lnet/minecraft/client/renderer/RenderType;"))
	private static RenderType getArmorGlint(RenderType prev) {
		return ColorRunesModule.getArmorGlint();
	}

	@ModifyExpressionValue(method = "getArmorFoilBuffer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;armorEntityGlint()Lnet/minecraft/client/renderer/RenderType;"))
	private static RenderType getArmorEntityGlint(RenderType prev) {
		return ColorRunesModule.getArmorEntityGlint();
	}

	@ModifyExpressionValue(method = "getFoilBuffer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;glintTranslucent()Lnet/minecraft/client/renderer/RenderType;"))
	private static RenderType getGlintTranslucent(RenderType prev) {
		return ColorRunesModule.getGlintTranslucent();
	}

	@ModifyExpressionValue(method = "getFoilBuffer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;glint()Lnet/minecraft/client/renderer/RenderType;"))
	private static RenderType getGlint(RenderType prev) {
		return ColorRunesModule.getGlint();
	}

	@ModifyExpressionValue(method = "getFoilBuffer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;entityGlint()Lnet/minecraft/client/renderer/RenderType;"))
	private static RenderType getEntityGlint(RenderType prev) {
		return ColorRunesModule.getEntityGlint();
	}

	@ModifyExpressionValue(method = "getFoilBufferDirect", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;glintDirect()Lnet/minecraft/client/renderer/RenderType;"))
	private static RenderType getGlintDirect(RenderType prev) {
		return ColorRunesModule.getGlintDirect();
	}

	@ModifyExpressionValue(method = "getFoilBufferDirect", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;entityGlintDirect()Lnet/minecraft/client/renderer/RenderType;"))
	private static RenderType getEntityGlintDirect(RenderType prev) {
		return ColorRunesModule.getEntityGlintDirect();
	}

	@ModifyConstant(method = "renderQuadList", constant = @Constant(floatValue = 1F))
	public float renderQuads(float constant) {
		return ItemSharingModule.alphaValue * constant;
	}
}
