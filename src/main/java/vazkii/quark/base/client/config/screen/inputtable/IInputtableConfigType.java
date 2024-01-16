package vazkii.quark.base.client.config.screen.inputtable;

import java.util.List;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.quark.api.config.IConfigElement;
import vazkii.quark.base.client.config.screen.CategoryScreen;
import vazkii.quark.base.client.config.screen.WidgetWrapper;
import vazkii.quark.base.client.config.screen.widgets.IWidgetProvider;
import vazkii.quark.base.client.config.screen.widgets.PencilButton;
import vazkii.quark.base.module.config.type.IConfigType;

public interface IInputtableConfigType<T extends IInputtableConfigType<T>> extends IWidgetProvider, IConfigType {

	T copy();
	void inherit(T other, boolean committing);
	void inheritDefaults(T other);
	
	void markDirty(boolean dirty);
	boolean isDirty();
	
	@OnlyIn(Dist.CLIENT)
	static void addPencil(CategoryScreen parent, IConfigElement element, List<WidgetWrapper> widgets, Supplier<Screen> screenSupplier) {
		Minecraft minecraft = Minecraft.getInstance();
		widgets.add(new WidgetWrapper(new PencilButton(230, 3, b -> minecraft.setScreen(screenSupplier.get()))));
	}

}
