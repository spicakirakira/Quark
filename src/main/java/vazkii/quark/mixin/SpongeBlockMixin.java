package vazkii.quark.mixin;

import net.minecraft.world.level.block.SpongeBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import vazkii.quark.content.tweaks.module.SpongesBetterModule;

@Mixin(SpongeBlock.class)
public class SpongeBlockMixin {

	@ModifyConstant(method = "removeWaterBreadthFirstSearch", constant = @Constant(intValue = 64))
	public int getDrainLimit(int limit) {
		return SpongesBetterModule.drainLimit(limit);
	}

	@ModifyConstant(method = "removeWaterBreadthFirstSearch", constant = @Constant(intValue = 6))
	public int getCrawlLimit(int limit) {
		return SpongesBetterModule.crawlLimit(limit);
	}

}
