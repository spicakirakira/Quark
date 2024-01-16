package vazkii.quark.base.client.config.screen.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ObjectSelectionList;
import vazkii.quark.base.client.config.screen.AbstractScrollingWidgetScreen;
import vazkii.quark.base.client.config.screen.WidgetWrapper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class ScrollableWidgetList<S extends AbstractScrollingWidgetScreen, E extends ScrollableWidgetList.Entry<E>> extends ObjectSelectionList<E> {

	public final S parent;

	public ScrollableWidgetList(S parent) {
		super(Minecraft.getInstance(), parent.width, parent.height, 40, parent.height - 40, 30);
		this.parent = parent;
	}

	public void populate(Consumer<AbstractWidget> widgetConsumer) {
		List<E> children = children();
		children.clear();

		findEntries();
		for(E e : children)
			e.commitWidgets(widgetConsumer);
	}

	protected abstract void findEntries();

	@Override
	protected int getScrollbarPosition() {
		return super.getScrollbarPosition() + 20;
	}

	@Override
	public int getRowWidth() {
		return super.getRowWidth() + 50;
	}

	@Override
	protected boolean isFocused() {
		return false;
	}

	public static abstract class Entry<E extends Entry<E>> extends ObjectSelectionList.Entry<E> {

		public List<WidgetWrapper> children = new ArrayList<>();

		public final void commitWidgets(Consumer<AbstractWidget> consumer) {
			children.stream().map(c -> c.widget).forEach(consumer);
		}

		@Override
		public void render(@Nonnull PoseStack mstack, int index, int rowTop, int rowLeft, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean hovered, float partialTicks) {
			children.forEach(c -> c.updatePosition(rowLeft, rowTop));
		}

		public void drawBackground(PoseStack mstack, int index, int rowTop, int rowLeft, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean hovered) {
			if(index % 2 == 0)
				fill(mstack, rowLeft, rowTop, rowLeft + rowWidth, rowTop + rowHeight, 0x66000000);

			if(hovered) {
				fill(mstack, rowLeft, rowTop, rowLeft + 1, rowTop + rowHeight, 0xFFFFFFFF);
				fill(mstack, rowLeft + rowWidth - 1, rowTop, rowLeft + rowWidth, rowTop + rowHeight, 0xFFFFFFFF);

				fill(mstack, rowLeft, rowTop, rowLeft + rowWidth, rowTop + 1, 0xFFFFFFFF);
				fill(mstack, rowLeft, rowTop + rowHeight - 1, rowLeft + rowWidth, rowTop + rowHeight, 0xFFFFFFFF);
			}
		}

	}

}
