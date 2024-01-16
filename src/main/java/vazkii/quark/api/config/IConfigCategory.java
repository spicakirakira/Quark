package vazkii.quark.api.config;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public interface IConfigCategory extends IConfigElement {

	IConfigCategory addCategory(String name, @Nonnull String comment, Object holderObject);
	<T> IConfigElement addEntry(ConfigValue<T> value, T default_, Supplier<T> getter, @Nonnull String comment, @Nonnull Predicate<Object> restriction);

	default <T> void addEntry(ConfigValue<T> forgeValue) {
		addEntry(forgeValue, forgeValue.get(), forgeValue::get, "", o -> true);
	}

	default IConfigCategory addCategory(String name) {
		return addCategory(name, "", null);
	}

	// getters you probably don't have any use for
	String getPath();
	int getDepth();
	List<IConfigElement> getSubElements();

	// probably stuff you shouldn't touch

	void updateDirty();
	void close();


}
