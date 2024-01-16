package vazkii.quark.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.world.inventory.LoomMenu;
import vazkii.quark.content.tweaks.module.MoreBannerLayersModule;

@Mixin(LoomMenu.class)
public class LoomMenuMixin {

	@ModifyConstant(method = "slotsChanged", constant = @Constant(intValue = 6))
	public int getLimit(int curr) {
		return MoreBannerLayersModule.getLimit(curr);
	}
	
}
