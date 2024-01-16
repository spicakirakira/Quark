package vazkii.quark.base.item;

import java.util.function.BooleanSupplier;

import javax.annotation.Nullable;

import net.minecraft.world.item.Item;
import vazkii.quark.base.module.QuarkModule;

public interface IQuarkItem {

	@Nullable
	QuarkModule getModule();

	default IQuarkItem setCondition(BooleanSupplier condition) {
		return this;
	}

	default boolean doesConditionApply() {
		return true;
	}
	
	default Item getItem() {
		return (Item) this;
	}

	default boolean isEnabled() {
		QuarkModule module = getModule();
		return module != null && module.enabled && doesConditionApply();
	}
	
}
