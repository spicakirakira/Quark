package vazkii.quark.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Slime;
import vazkii.quark.content.tweaks.module.SlimesToMagmaCubesModule;

@Mixin(Slime.class)
public class SlimeMixin {

	@Redirect(method = "remove", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/Slime;getType()Lnet/minecraft/world/entity/EntityType;"))
	public EntityType<? extends Slime> getRealType(Slime slime) {
		return SlimesToMagmaCubesModule.getSlimeType(slime);
	}
	
}
