package vazkii.quark.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Tier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import vazkii.quark.content.tweaks.module.GoldToolsHaveFortuneModule;

@Mixin(DiggerItem.class)
public class DiggerItemMixin {

	@ModifyExpressionValue(method = "isCorrectToolForDrops(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/block/state/BlockState;)Z",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/DiggerItem;getTier()Lnet/minecraft/world/item/Tier;"))
	private Tier getTier(Tier prev) {
		return GoldToolsHaveFortuneModule.getEffectiveTier((DiggerItem) (Object) this, prev);
	}

}
