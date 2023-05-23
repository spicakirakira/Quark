package vazkii.quark.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import vazkii.quark.content.tweaks.module.DiamondRepairModule;

@Mixin(AnvilMenu.class)
public class AnvilMenuMixin {

	@Redirect(method = "createResult()V", at = @At(value = "INVOKE", 
			target = "Lnet/minecraft/world/item/Item;isValidRepairItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"))
	public boolean isValidRepairItem(Item item, ItemStack stack, ItemStack repairStack) {
		return DiamondRepairModule.isValidRepairItem(item, stack, repairStack);
	}
	
}
