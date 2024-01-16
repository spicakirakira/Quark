package vazkii.quark.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import vazkii.quark.content.tools.module.PickarangModule;

@Mixin(Entity.class)
public class EntityMixin {

	@ModifyReturnValue(method = "fireImmune", at = @At("RETURN"))
	private boolean isImmuneToFire(boolean prev) {
		return PickarangModule.getIsFireResistant(prev, (Entity) (Object) this);
	}

}
