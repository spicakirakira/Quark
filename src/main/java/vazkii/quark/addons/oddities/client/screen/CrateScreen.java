package vazkii.quark.addons.oddities.client.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import vazkii.quark.addons.oddities.inventory.CrateMenu;
import vazkii.quark.addons.oddities.module.CrateModule;
import vazkii.quark.base.Quark;
import vazkii.quark.base.client.handler.InventoryButtonHandler;
import vazkii.quark.base.client.handler.InventoryButtonHandler.ButtonTargetType;
import vazkii.quark.base.handler.MiscUtil;
import vazkii.quark.content.client.module.ChestSearchingModule;

import javax.annotation.Nonnull;
import java.util.List;

public class CrateScreen extends AbstractContainerScreen<CrateMenu> {

	private static final ResourceLocation TEXTURE = new ResourceLocation(Quark.MOD_ID, "textures/gui/crate.png");

	private List<Rect2i> extraAreas;

	public CrateScreen(CrateMenu container, Inventory inv, Component component) {
		super(container, inv, component);

		int inventoryRows = CrateMenu.numRows;
		imageHeight = 114 + inventoryRows * 18;
		inventoryLabelY = imageHeight - 94;
	}

	@Override
	protected void init() {
		super.init();

		int i = (width - imageWidth) / 2;
		int j = (height - imageHeight) / 2;
		extraAreas = Lists.newArrayList(new Rect2i(i + imageWidth, j, 23, 136));
	}

	public List<Rect2i> getExtraAreas() {
		return extraAreas;
	}

	@Override
	public void render(@Nonnull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		renderTooltip(matrixStack, mouseX, mouseY);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
		menu.scroll(delta < 0, true);
		return true;
	}

	@Override
	protected void renderBg(@Nonnull PoseStack matrixStack, float partialTicks, int x, int y) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, TEXTURE);

		int i = (width - imageWidth) / 2;
		int j = (height - imageHeight) / 2;
		blit(matrixStack, i, j, 0, 0, imageWidth + 20, imageHeight);

		int maxScroll = (menu.getStackCount() / CrateMenu.numCols) * CrateMenu.numCols;
		int currScroll = (menu.scroll * 95) / Math.max(1, maxScroll);

		int u = 232 + (maxScroll == 0 ? 12 : 0);
		int by = j + 18 + currScroll;
		blit(matrixStack, i + imageWidth, by, u, 0, 12, 15);

		if(!ChestSearchingModule.searchEnabled) {
			String s = menu.getTotal() + "/" + CrateModule.maxItems;

			int color = MiscUtil.getGuiTextColor("crate_count");
			font.draw(matrixStack, s, i + this.imageWidth - font.width(s) - 8 - InventoryButtonHandler.getActiveButtons(ButtonTargetType.CONTAINER_INVENTORY).size() * 12, j + 6, color);
		}
	}

	@Override
	protected void renderLabels(@Nonnull PoseStack poseStack, int mouseX, int mouseY) {
		int color = MiscUtil.getGuiTextColor("crate_count");

		this.font.draw(poseStack, this.title, (float)this.titleLabelX, (float)this.titleLabelY, color);
		this.font.draw(poseStack, this.playerInventoryTitle, (float)this.inventoryLabelX, (float)this.inventoryLabelY, color);
	}

}
