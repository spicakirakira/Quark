package vazkii.quark.base.client.config.screen.inputtable;

import java.util.Arrays;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.gui.widget.ForgeSlider;
import vazkii.quark.api.config.IConfigCategory;
import vazkii.quark.api.config.IConfigElement;
import vazkii.quark.base.module.config.type.inputtable.ConvulsionMatrixConfig;

public class ConvulsionMatrixInputScreen extends AbstractInputtableConfigTypeScreen<ConvulsionMatrixConfig> {

	public ConvulsionMatrixInputScreen(Screen parent, ConvulsionMatrixConfig original, IConfigElement element, IConfigCategory category) {
		super(parent, original, element, category);
	}

	@Override
	protected void onInit() {
		int w = 70;
		int p = 12;
		int x = width / 2 - 33;
		int y = 55;

		Component prefix = Component.literal("");
		Component suffix = Component.literal("");

		for(int i = 0; i < 9; i++)
			addRenderableWidget(new ForgeSlider(x + w * (i % 3), y + 25 * (i / 3), w - p, 20, prefix, suffix, 0f, 2f, original.colorMatrix[i], 0, 1, false) {
				@Override
				protected void applyValue() {
					onSlide(this);
				}
			});

		int i = 0;
		for(String s : mutable.params.presetMap.keySet()) {
			addRenderableWidget(new Button(x + (w * i), y + 115, w - p, 20, Component.literal(s), this::onSlide));
			i++;
		}
	}

	@Override
	public void render(@Nonnull PoseStack mstack, int mouseX, int mouseY, float partialTicks) {
		super.render(mstack, mouseX, mouseY, partialTicks);

		int x = width / 2 - 203;
		int y = 10;
		int size = 60;

		int titleLeft = width / 2 + 66;
		drawCenteredString(mstack, font, Component.literal(mutable.params.name).withStyle(ChatFormatting.BOLD), titleLeft, 20, 0xFFFFFF);
		drawCenteredString(mstack, font, Component.literal("Presets"), titleLeft, 155, 0xFFFFFF);

		int sliders = 0;
		boolean needsUpdate = false;
		for(Widget w : renderables)
			if(w instanceof ForgeSlider s) {
				double val = correct(s);
				double curr = mutable.colorMatrix[sliders];
				if(curr != val) {
					mutable.colorMatrix[sliders] = val;
					needsUpdate = true;
				}

				String displayVal = String.format("%.2f", val);
				font.drawShadow(mstack, displayVal, s.x + (float) (s.getWidth() / 2 - font.width(displayVal) / 2) , s.y + 6, 0xFFFFFF);

				switch (sliders) {
				case 0 -> {
					font.drawShadow(mstack, "R =", s.x - 20, s.y + 5, 0xFF0000);
					font.drawShadow(mstack, "R", s.x + (float) (s.getWidth() / 2 - 2), s.y - 12, 0xFF0000);
				}
				case 1 -> font.drawShadow(mstack, "G", s.x + (float) (s.getWidth() / 2 - 2), s.y - 12, 0x00FF00);
				case 2 -> font.drawShadow(mstack, "B", s.x + (float) (s.getWidth() / 2 - 2), s.y - 12, 0x0077FF);
				case 3 -> font.drawShadow(mstack, "G =", s.x - 20, s.y + 5, 0x00FF00);
				case 6 -> font.drawShadow(mstack, "B =", s.x - 20, s.y + 5, 0x0077FF);
				default -> {
				}
				}
				if((sliders % 3) != 0)
					font.drawShadow(mstack, "+", s.x - 9, s.y + 5, 0xFFFFFF);

				sliders++;
			}

		String[] biomes = mutable.params.biomeNames;
		int[] colors = mutable.params.testColors;
		int[] folliageColors =  mutable.params.folliageTestColors;
		boolean renderFolliage = mutable.params.shouldDisplayFolliage();

		for(int i = 0; i < biomes.length; i++) {
			String name = biomes[i];
			int color = colors[i];

			int convolved = mutable.convolve(color);

			int folliage, convolvedFolliage = 0;
			if(renderFolliage) {
				folliage = folliageColors[i];
				convolvedFolliage = mutable.convolve(folliage);
			}

			int cx = x + (i % 2) * (size + 5);
			int cy = y + (i / 2) * (size + 5);

			fill(mstack, cx - 1, cy - 1, cx + size + 1, cy + size + 1, 0xFF000000);
			fill(mstack, cx, cy, cx + size, cy + size, convolved);
			fill(mstack, cx + size / 2 - 1, cy + size / 2 - 1, cx + size, cy + size, 0x22000000);

			if(renderFolliage)
				fill(mstack, cx + size / 2, cy + size / 2, cx + size, cy + size, convolvedFolliage);

			font.draw(mstack, name, cx + 2, cy + 2, 0x55000000);

			if(renderFolliage) {
				minecraft.getItemRenderer().renderGuiItem(new ItemStack(Items.OAK_SAPLING), cx + size - 18, cy + size - 16);
				mstack.pushPose();
				mstack.translate(0, 0, 999);
				fill(mstack, cx + size / 2, cy + size / 2, cx + size, cy + size, convolvedFolliage & 0x55FFFFFF);
				mstack.popPose();
			}
		}

		if(needsUpdate)
			update();
	}

	private double correct(ForgeSlider s) {
		double val = s.getValue();
		val = correct(val, 1.0, s);
		val = correct(val, 0.5, s);
		val = correct(val, 1.5, s);
		return val;
	}

	private double correct(double val, double correct, ForgeSlider s) {
		if(Math.abs(val - correct) < 0.02) {
			s.setValue(correct);
			return correct;
		}
		return val;
	}

	private void onSlide(AbstractWidget widget) {
		String name = widget.getMessage().getString();

		double[] matrix = null;
		if(mutable.params.presetMap.containsKey(name))
			matrix = mutable.params.presetMap.get(name);

		if(matrix != null) {
			int sliders = 0; 
			mutable.colorMatrix = Arrays.copyOf(matrix, matrix.length);

			for(Widget w : renderables)
				if(w instanceof ForgeSlider s) {
					s.setValue(matrix[sliders]);
					sliders++;
				}
		}

		update();
	}

}
