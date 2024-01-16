package org.violetmoon.quark.mixin.mixins;

import net.minecraft.world.level.block.SpongeBlock;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import org.violetmoon.quark.content.tweaks.module.ImprovedSpongesModule;

@Mixin(SpongeBlock.class)
public class SpongeBlockMixin {

	@ModifyConstant(method = "removeWaterBreadthFirstSearch", constant = @Constant(intValue = 65))
	public int getDrainLimit(int limit) {
		return ImprovedSpongesModule.drainLimit(limit);
	}

	@ModifyConstant(method = "removeWaterBreadthFirstSearch", constant = @Constant(intValue = 6))
	public int getCrawlLimit(int limit) {
		return ImprovedSpongesModule.crawlLimit(limit);
	}

}
