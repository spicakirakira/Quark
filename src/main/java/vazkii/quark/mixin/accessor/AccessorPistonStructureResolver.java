package vazkii.quark.mixin.accessor;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PistonStructureResolver.class)
public interface AccessorPistonStructureResolver {

	@Accessor("extending")
	boolean quark$extending();

	@Accessor("level")
	Level quark$level();

	@Accessor("pistonPos")
	BlockPos quark$pistonPos();

	@Accessor("pistonDirection")
	Direction quark$pistonDirection();
}
