package vazkii.quark.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.predicates.BonusLevelTableCondition;
import vazkii.quark.content.tweaks.module.GoldToolsHaveFortuneModule;

@Mixin(BonusLevelTableCondition.class)
public class BonusLevelTableConditionMixin {

	@Redirect(method = "test(Lnet/minecraft/world/level/storage/loot/LootContext;)Z", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getItemEnchantmentLevel(Lnet/minecraft/world/item/enchantment/Enchantment;Lnet/minecraft/world/item/ItemStack;)I"))
	public int getLevel(Enchantment enchantment, ItemStack stack) {
		int val = EnchantmentHelper.getItemEnchantmentLevel(enchantment, stack);
		return GoldToolsHaveFortuneModule.getActualEnchantmentLevel(enchantment, stack, val);
	}

}
