package vazkii.quark.integration.mixin.lootr;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import noobanidus.mods.lootr.config.ConfigManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import vazkii.quark.integration.lootr.ILootrIntegration;

@Pseudo
@Mixin(value = ConfigManager.class, remap = false)
public class ConfigManagerMixin {
	@ModifyVariable(method = "addSafeReplacement", at = @At("HEAD"), argsOnly = true, remap = false)
	private static Block replacement(Block original, ResourceLocation location) {
		Block block = ForgeRegistries.BLOCKS.getValue(location);
		if (block != null) {
			Block lootrVariant = ILootrIntegration.INSTANCE.lootrVariant(block);
			if (lootrVariant != null)
				return lootrVariant;
		}

		return original;
	}
}
