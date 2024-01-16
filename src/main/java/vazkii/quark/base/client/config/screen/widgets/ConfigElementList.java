package vazkii.quark.base.client.config.screen.widgets;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import vazkii.quark.api.config.IConfigElement;
import vazkii.quark.api.config.IConfigObject;
import vazkii.quark.base.client.config.ConfigCategory;
import vazkii.quark.base.client.config.screen.CategoryScreen;
import vazkii.quark.base.client.handler.TopLayerTooltipHandler;

public class ConfigElementList<T extends IConfigElement & IWidgetProvider> extends ScrollableWidgetList<CategoryScreen, ConfigElementList.Entry<T>> {

	public ConfigElementList(CategoryScreen parent) {
		super(parent);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void findEntries() {
		boolean hadObjects = false;
		boolean isObject = true;

		for(IConfigElement elm : parent.category.getSubElements()) {
			boolean wasObject = isObject;
			isObject = elm instanceof IConfigObject;

			if(wasObject && !isObject && hadObjects)
				addEntry(new Entry<>(parent, null)); // separator

			Entry<T> entry = new Entry<>(parent, (T) elm);
			addEntry(entry);

			hadObjects = hadObjects || isObject;
		}
	}

	public static final class Entry<T extends IConfigElement & IWidgetProvider> extends ScrollableWidgetList.Entry<Entry<T>> {

		private final T element;

		public Entry(CategoryScreen parent, T element) {
			this.element = element;

			if(element != null)
				element.addWidgets(parent, element, children);
		}

		@Override
		public void render(@Nonnull PoseStack mstack, int index, int rowTop, int rowLeft, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean hovered, float partialTicks) {
			super.render(mstack, index, rowTop, rowLeft, rowWidth, rowHeight, mouseX, mouseY, hovered, partialTicks);

			Minecraft mc = Minecraft.getInstance();

			if(element != null) {
				int left = rowLeft + 10;
				int top = rowTop + 4;

				int effIndex = index + 1;
				if(element instanceof ConfigCategory)
					effIndex--; // compensate for the divider
				drawBackground(mstack, effIndex, rowTop, rowLeft, rowWidth, rowHeight, mouseX, mouseY, hovered);

				String name = element.getGuiDisplayName();
				if(element.isDirty())
					name += ChatFormatting.GOLD + "*";

				int len = mc.font.width(name);
				int maxLen = rowWidth - 85;
				String originalName = null;
				if(len > maxLen) {
					originalName = name;
					do {
						name = name.substring(0, name.length() - 1);
						len = mc.font.width(name);
					} while(len > maxLen);

					name += "...";
				}

				List<String> tooltip = element.getTooltip();
				if(originalName != null) {
					if(tooltip == null) {
						tooltip = new LinkedList<>();
						tooltip.add(originalName);
					} else {
						tooltip.add(0, "");
						tooltip.add(0, originalName);
					}
				}

				if(tooltip != null) {
					int hoverLeft = left + mc.font.width(name + " ");
					int hoverRight = hoverLeft + mc.font.width("(?)");

					name += (ChatFormatting.AQUA + " (?)");
					if(mouseX >= hoverLeft && mouseX < hoverRight && mouseY >= top && mouseY < (top + 10))
						TopLayerTooltipHandler.setTooltip(tooltip, mouseX, mouseY);
				}

				mc.font.drawShadow(mstack, name, left, top, 0xFFFFFF);
				mc.font.drawShadow(mstack, element.getSubtitle(), left, top + 10, 0x999999);
			} else {
				String s = I18n.get("quark.gui.config.subcategories");
				mc.font.drawShadow(mstack, s, rowLeft + (float) (rowWidth / 2 - mc.font.width(s) / 2), rowTop + 7, 0x6666FF);
			}
		}

		@Nonnull
		@Override
		public Component getNarration() {
			return Component.literal(element == null ? "" : element.getGuiDisplayName());
		}

	}

}
