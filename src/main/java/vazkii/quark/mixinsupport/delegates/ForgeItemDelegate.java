package vazkii.quark.mixinsupport.delegates;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import vazkii.quark.content.tweaks.module.GoldToolsHaveFortuneModule;
import vazkii.quark.mixinsupport.DelegateInterfaceTarget;
import vazkii.quark.mixinsupport.DelegateReturnValueTarget;

import java.util.Map;

@DelegateInterfaceTarget
public class ForgeItemDelegate {
	@DelegateReturnValueTarget("getEnchantmentLevel(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/enchantment/Enchantment;)I")
	public static int getEnchantmentLevel(int previous, ItemStack stack, Enchantment enchantment) {
		return GoldToolsHaveFortuneModule.getActualEnchantmentLevel(enchantment, stack, previous);
	}

	@DelegateReturnValueTarget("getAllEnchantments(Lnet/minecraft/world/item/ItemStack;)Ljava/util/Map;")
	public static Map<Enchantment, Integer> getAllEnchantments(Map<Enchantment, Integer> previous, ItemStack stack) {
		GoldToolsHaveFortuneModule.addEnchantmentsIfMissing(stack, previous);
		return previous;
	}
}
