package vazkii.quark.base.client.config.screen.inputtable;

import net.minecraft.client.gui.screens.Screen;
import vazkii.quark.api.config.IConfigCategory;
import vazkii.quark.api.config.IConfigElement;
import vazkii.quark.base.client.config.screen.AbstractInputScreen;

public abstract class AbstractInputtableConfigTypeScreen<T extends IInputtableConfigType<T>> extends AbstractInputScreen<T> {

	final T original;
	final T mutable;
	final IConfigElement element;
	final IConfigCategory category;

	public AbstractInputtableConfigTypeScreen(Screen parent, T original, IConfigElement element, IConfigCategory category) {
		super(parent);

		this.original = original;
		this.mutable = original.copy();
		this.element = element;
		this.category = category;
	}
	
	@Override
	protected T compute() {
		return mutable;
	}
	
	@Override
	protected boolean isDirty() {
		return !original.equals(mutable);
	}

	@Override
	protected void commit() {
		original.inherit(mutable, true);
		original.markDirty(true);
	}
	
	@Override
	protected void setDefault() {
		mutable.inheritDefaults(original);
	}

	@Override
	protected void reset() {
		mutable.inheritDefaults(mutable);
	}

	@Override
	protected boolean isErrored() {
		return false;
	}
	
}
