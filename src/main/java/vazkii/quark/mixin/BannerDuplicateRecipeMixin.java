package vazkii.quark.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.world.item.crafting.BannerDuplicateRecipe;
import vazkii.quark.content.tweaks.module.MoreBannerLayersModule;

@Mixin(BannerDuplicateRecipe.class)
public class BannerDuplicateRecipeMixin {

	@ModifyConstant(method = "matches(Lnet/minecraft/world/inventory/CraftingContainer;Lnet/minecraft/world/level/Level;)Z", constant = @Constant(intValue = 6))
	public int getLimitMatches(int curr) {
		return MoreBannerLayersModule.getLimit(curr);
	}

	@ModifyConstant(method = "assemble(Lnet/minecraft/world/inventory/CraftingContainer;)Lnet/minecraft/world/item/ItemStack;", constant = @Constant(intValue = 6))
	public int getLimitAssemble(int curr) {
		return MoreBannerLayersModule.getLimit(curr);
	}

}
