package vazkii.quark.api;

import java.util.function.Predicate;

import net.minecraft.world.item.ItemStack;

/**
 * Implement this on an Item to change its behavior with the quark usage ticker.
 */
public interface IUsageTickerOverride {

	default int getUsageTickerCountForItem(ItemStack stack, Predicate<ItemStack> target) {
		return 0;
	}

	default boolean shouldUsageTickerCheckMatchSize(ItemStack stack) {
		return false;
	}

	ItemStack getUsageTickerItem(ItemStack stack);

}
