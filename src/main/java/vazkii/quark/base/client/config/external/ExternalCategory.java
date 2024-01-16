package vazkii.quark.base.client.config.external;

import vazkii.quark.api.config.IConfigCategory;
import vazkii.quark.api.config.IExternalCategory;
import vazkii.quark.base.client.config.ConfigCategory;

import javax.annotation.Nonnull;
import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ExternalCategory extends ConfigCategory implements IExternalCategory {

	private final Map<String, IConfigCategory> topLevelCategories = new LinkedHashMap<>();

	private final Consumer<IExternalCategory> onChangedCallback;

	public ExternalCategory(String name, Consumer<IExternalCategory> onChangedCallback, IConfigCategory parent) {
		super(name, "", parent, null);
		this.onChangedCallback = onChangedCallback;
	}

	@Override
	public IExternalCategory addTopLevelCategory(String name, Consumer<IExternalCategory> onChangedCallback) {
		ExternalCategory category = (ExternalCategory) addCategory(new ExternalCategory(name, onChangedCallback, this));
		topLevelCategories.put(name, category);
		return category;
	}

	@Override
	public void commit() {
		onChangedCallback.accept(this);
	}

	@Override
	public IConfigCategory addCategory(String name, @Nonnull String comment, Object holderObject) {
		IConfigCategory category = super.addCategory(name, comment, holderObject);
		topLevelCategories.put(name, category);
		return category;
	}

	@Override
	public void print(String pad, PrintStream stream) {
		subElements.forEach(e -> e.print(pad, stream));
	}

	@Override
	public Map<String, IConfigCategory> getTopLevelCategories() {
		return topLevelCategories;
	}

}
