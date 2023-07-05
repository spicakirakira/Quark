package vazkii.quark.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import vazkii.quark.content.tweaks.module.GoldToolsHaveFortuneModule;

@Mixin(ApplyBonusCount.class)
public class ApplyBonusCountMixin {

	@Redirect(method = "run", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getItemEnchantmentLevel(Lnet/minecraft/world/item/enchantment/Enchantment;Lnet/minecraft/world/item/ItemStack;)I"))
	public int getLevel(Enchantment enchantment, ItemStack stack) {
		int val = EnchantmentHelper.getItemEnchantmentLevel(enchantment, stack);
		return GoldToolsHaveFortuneModule.getActualEnchantmentLevel(enchantment, stack, val);
	}

}
