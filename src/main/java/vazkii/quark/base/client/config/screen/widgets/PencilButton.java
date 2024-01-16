package vazkii.quark.base.client.config.screen.widgets;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import vazkii.quark.base.handler.MiscUtil;

public class PencilButton extends Button {

	public PencilButton(int x, int y, OnPress pressable) {
		super(x, y, 20, 20, Component.literal(""), pressable);
	}

	@Override
	public void renderButton(@Nonnull PoseStack mstack, int mouseX, int mouseY, float partialTicks) {
		super.renderButton(mstack, mouseX, mouseY, partialTicks);

		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, MiscUtil.GENERAL_ICONS);

		int u = 32;
		int v = 93;

		blit(mstack, x + 2, y + 1, u, v, 16, 16);
	}

}
