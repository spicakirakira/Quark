package vazkii.quark.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Slime;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import vazkii.quark.content.tweaks.module.SlimesToMagmaCubesModule;

@Mixin(Slime.class)
public class SlimeMixin {

	@ModifyExpressionValue(method = "remove", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/Slime;getType()Lnet/minecraft/world/entity/EntityType;"))
	public EntityType<? extends Slime> getRealType(EntityType<? extends Slime> prev) {
		return SlimesToMagmaCubesModule.getSlimeType(prev, (Slime) (Object) this);
	}

}
