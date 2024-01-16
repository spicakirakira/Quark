package vazkii.quark.base.client.config;

import java.io.PrintStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.text.WordUtils;

import net.minecraft.client.resources.language.I18n;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import vazkii.quark.api.config.IConfigCategory;
import vazkii.quark.api.config.IConfigElement;
import vazkii.quark.api.config.IConfigObject;
import vazkii.quark.base.client.config.external.ExternalCategory;
import vazkii.quark.base.client.config.screen.CategoryScreen;
import vazkii.quark.base.client.config.screen.WidgetWrapper;
import vazkii.quark.base.client.config.screen.inputtable.IInputtableConfigType;
import vazkii.quark.base.client.config.screen.widgets.IWidgetProvider;
import vazkii.quark.base.client.config.screen.widgets.PencilButton;
import vazkii.quark.base.module.config.type.IConfigType;

public class ConfigCategory extends AbstractConfigElement implements IConfigCategory, IWidgetProvider {

	public final List<IConfigElement> subElements = new LinkedList<>();

	private final String path;
	private final int depth;
	protected final Object holderObject;

	private boolean dirty = false;

	public ConfigCategory(String name, String comment, IConfigCategory parent, Object holderObject) {
		super(name, comment, parent);

		this.holderObject = holderObject;
		if(parent == null || (parent instanceof ExternalCategory)) {
			path = name;
			depth = 0;
		} else {
			path = String.format("%s.%s", parent.getPath(), name);
			depth = 1 + parent.getDepth();
		}
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public int getDepth() {
		return depth;
	}

	@Override
	public List<IConfigElement> getSubElements() {
		return subElements;
	}

	@Override
	public String getDefaultGuiDisplayName() {
		return WordUtils.capitalize(getName().replaceAll("_", " "));
	}

	@Override
	public void updateDirty() {
		dirty = shouldBeDirty();

		if(parent != null)
			parent.updateDirty();
	}
	
	protected boolean shouldBeDirty() {
		if(isInputtable())
			return ((IInputtableConfigType<?>) holderObject).isDirty();
		
		for(IConfigElement sub : subElements)
			if(sub.isDirty())
				return true;
		
		return false;
	}

	@Override
	public void clean() {
		subElements.forEach(IConfigElement::clean);
		dirty = false;
	}

	@Override
	public void save() {
		for(IConfigElement element : subElements)
			if(isInputtable() || element.isDirty())
				element.save();
	}
	
	private boolean isInputtable() {
		return holderObject instanceof IInputtableConfigType<?>;
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}

	@Override
	public void refresh() {
		subElements.forEach(IConfigElement::refresh);
	}

	@Override
	public void reset(boolean hard) {
		subElements.forEach(e -> e.reset(hard));
	}

	@Override
	public IConfigCategory addCategory(String name, @Nonnull String comment, Object holderObject) {
		ConfigCategory newCategory = new ConfigCategory(name, comment, this, holderObject); 
		if(holderObject instanceof IConfigType configType)
			configType.setCategory(newCategory);

		return addCategory(newCategory);
	}

	public IConfigCategory addCategory(IConfigCategory category) {
		subElements.add(category);
		return category;
	}

	@Override
	public <T> IConfigObject<T> addEntry(ConfigValue<T> value, T default_, Supplier<T> getter, @Nonnull String comment, @Nonnull Predicate<Object> restriction) {
		IConfigObject<T> obj = ConfigObject.create(value, comment, default_, getter, restriction, this);
		addEntry(obj, default_);
		return obj;
	}

	public <T> void addEntry(IConfigObject<T> obj, T default_) {
		subElements.add(obj);
	}

	@Override
	public void close() {
		subElements.removeIf(e -> e instanceof ConfigCategory && ((ConfigCategory) e).subElements.isEmpty());
		Collections.sort(subElements);
	}

	@Override
	public void addWidgets(CategoryScreen parent, IConfigElement element, List<WidgetWrapper> widgets) {
		if(holderObject instanceof IWidgetProvider widgetProvider)
			widgetProvider.addWidgets(parent, element, widgets);
		else widgets.add(new WidgetWrapper(new PencilButton(230, 3, parent.categoryLink(this))));
	}

	@Override
	public void print(String pad, PrintStream stream) {
		stream.println();
		super.print(pad, stream);
		stream.printf("%s[%s]%n", pad, path);

		final String newPad = String.format("\t%s", pad);
		subElements.forEach(e -> e.print(newPad, stream));
	}

	@Override
	public String getSubtitle() {
		String ret;
		if(holderObject != null && holderObject instanceof IWidgetProvider)
			ret = ((IWidgetProvider) holderObject).getSubtitle();
		else {
			int size = subElements.size();
			ret = (size == 1 ? I18n.get("quark.gui.config.onechild") : I18n.get("quark.gui.config.nchildren", subElements.size()));
		}

		if(ret.length() > 30)
			ret = ret.substring(0, 27) + "...";
		return ret;
	}

	@Override
	public int compareTo(@Nonnull IConfigElement o) {
		if(o == this)
			return 0;
		if(!(o instanceof ConfigCategory cat))
			return 1;

		return name.compareTo(cat.name);
	}

}
