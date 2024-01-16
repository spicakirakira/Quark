package vazkii.quark.mixin;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.quark.content.experimental.module.EnchantmentsBegoneModule;

@Mixin(EnchantedBookItem.class)
public class EnchantedBookItemMixin {

	@Inject(method = "fillItemCategory", at = @At("RETURN"))
	private void canApply(CreativeModeTab tab, NonNullList<ItemStack> stacks, CallbackInfo ci) {
		EnchantmentsBegoneModule.begoneItems(stacks);
	}

}
