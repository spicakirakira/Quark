package vazkii.quark.base.client.config.screen;

import net.minecraft.client.gui.components.AbstractWidget;

public class WidgetWrapper {

	public final AbstractWidget widget;
	public final int relativeX, relativeY;
	
	public WidgetWrapper(AbstractWidget widget) {
		this.widget = widget;
		this.relativeX = widget.x;
		this.relativeY = widget.y;
	}
	
	public void updatePosition(int currX, int currY) {
		widget.x = currX + relativeX;
		widget.y = currY + relativeY;
		widget.visible = true;
	}
	
}
