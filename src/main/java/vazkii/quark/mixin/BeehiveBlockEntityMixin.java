package vazkii.quark.mixin;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = BeehiveBlockEntity.class)
public class BeehiveBlockEntityMixin {

	@WrapWithCondition(method = {"writeBees", "removeIgnoredBeeTags"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundTag;remove(Ljava/lang/String;)V"))
	private static boolean doNotRemoveUUIDOfBees(CompoundTag instance, String key) {
		return !key.equals("UUID");
	}

}
