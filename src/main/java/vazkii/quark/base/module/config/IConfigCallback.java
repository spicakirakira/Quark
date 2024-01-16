package vazkii.quark.base.module.config;

import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

import java.util.function.Predicate;
import java.util.function.Supplier;

public interface IConfigCallback {

	void push(String s, String comment, Object holderObject);
	void pop();

	<T> void addEntry(ConfigValue<T> value, T default_, Supplier<T> getter, String comment, Predicate<Object> restriction);

	final class Dummy implements IConfigCallback {

		@Override
		public void push(String s, String comment, Object holderObject) {
			// NO-OP
		}

		@Override
		public void pop() {
			// NO-OP
		}

		@Override
		public <T> void addEntry(ConfigValue<T> value, T default_, Supplier<T> getter, String comment, Predicate<Object> restriction) {
			// NO-OP
		}

	}

}
