package vazkii.quark.base.client.config.screen.inputtable;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.widget.ForgeSlider;
import vazkii.quark.api.config.IConfigCategory;
import vazkii.quark.api.config.IConfigElement;
import vazkii.quark.base.module.config.type.inputtable.RGBAColorConfig;
import vazkii.quark.base.module.config.type.inputtable.RGBColorConfig;

public class RGBColorInputScreen extends AbstractInputtableConfigTypeScreen<RGBColorConfig> {

	public RGBColorInputScreen(Screen parent, RGBColorConfig original, IConfigElement element, IConfigCategory category) {
		super(parent, original, element, category);
	}

	@Override
	protected void onInit() {
		int w = 100;
		int p = 12;
		int x = width / 2 - 110;
		int y = 55;

		Component prefix = Component.literal("");
		Component suffix = Component.literal("");

		int cnt = (original instanceof RGBAColorConfig ? 4 : 3);
		for(int i = 0; i < cnt; i++) {
			double curr = original.getElement(i);
			addRenderableWidget(new ForgeSlider(x , y + 25 * i, w - p, 20, prefix, suffix, 0f, 1f, curr, 0, 1, false) {
				@Override
				protected void applyValue() {
					update();
				}
			});
		}
	}

	@Override
	public void render(@Nonnull PoseStack mstack, int mouseX, int mouseY, float partialTicks) {
		super.render(mstack, mouseX, mouseY, partialTicks);

		int titleLeft = width / 2;
		drawCenteredString(mstack, font, Component.literal(category.getGuiDisplayName()).withStyle(ChatFormatting.BOLD), titleLeft, 20, 0xFFFFFF);
		drawCenteredString(mstack, font, Component.literal(element.getGuiDisplayName()), titleLeft, 30, 0xFFFFFF);

		int sliders = 0;
		boolean needsUpdate = false;
		for(Widget w : renderables)
			if(w instanceof ForgeSlider s) {
				double val = correct(s);
				double curr = mutable.getElement(sliders);
				if(curr != val) {
					mutable.setElement(sliders, val);
					needsUpdate = true;
				}

				String displayVal = String.format("%.2f", val);
				font.drawShadow(mstack, displayVal, s.x + (float) (s.getWidth() / 2 - font.width(displayVal) / 2) , s.y + 6, 0xFFFFFF);

				switch (sliders) {
				case 0 -> font.drawShadow(mstack, "R =", s.x - 20, s.y + 5, 0xFF0000);
				case 1 -> font.drawShadow(mstack, "G =", s.x - 20, s.y + 5, 0x00FF00);
				case 2 -> font.drawShadow(mstack, "B =", s.x - 20, s.y + 5, 0x0077FF);
				case 3 -> font.drawShadow(mstack, "A =", s.x - 20, s.y + 5, 0xFFFFFF);
				default -> {
				}
				}
				sliders++;
			}



		int cx = width / 2 + 20;
		int cy = 55;
		int size = 95;
		int color = mutable.getColor();

		fill(mstack, cx - 1, cy - 1, cx + size + 1, cy + size + 1, 0xFF000000);
		fill(mstack, cx, cy, cx + size, cy + size, 0xFF999999);
		fill(mstack, cx, cy, cx + size / 2, cy + size / 2, 0xFF666666);
		fill(mstack, cx + size / 2, cy + size / 2, cx + size, cy + size, 0xFF666666);

		fill(mstack, cx, cy, cx + size, cy + size, color);

		if(needsUpdate)
			update();
	}

	private double correct(ForgeSlider s) {
		double val = s.getValue();
		val = correct(val, 0.0, s);
		val = correct(val, 0.25, s);
		val = correct(val, 0.5, s);
		val = correct(val, 0.75, s);
		val = correct(val, 1.0, s);
		return val;
	}

	private double correct(double val, double correct, ForgeSlider s) {
		if(Math.abs(val - correct) < 0.02) {
			s.setValue(correct);
			return correct;
		}
		return val;
	}
}
