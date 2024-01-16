package org.violetmoon.quark.mixin.mixins;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import org.violetmoon.quark.content.tweaks.module.ArmedArmorStandsModule;

@Mixin(ArmorStand.class)
public class ArmorStandMixin {
	@ModifyConstant(method = "defineSynchedData", constant = @Constant(intValue = 0))
	private int asdasld(int orig) {
		if(!ArmedArmorStandsModule.staticEnabled)
			return orig;

		//try to be careful - this is a kinda scary looking mixin, lol
		SynchedEntityData data = ((Entity) (Object) this).getEntityData();
		if(data.hasItem(ArmorStand.DATA_CLIENT_FLAGS))
			return orig; //it's already been defined

		else
			return orig | ArmorStand.CLIENT_FLAG_SHOW_ARMS; // | 4
	}
}
