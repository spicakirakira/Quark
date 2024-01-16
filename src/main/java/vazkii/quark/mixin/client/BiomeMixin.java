package vazkii.quark.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import vazkii.quark.content.client.module.GreenerGrassModule;

@Mixin(Biome.class)
public class BiomeMixin {

	@ModifyReturnValue(method = {"getWaterColor", "getWaterFogColor"}, at = @At("RETURN"))
	private int getWaterColor(int prev) {
		return GreenerGrassModule.getWaterColor(prev);
	}

}
