package vazkii.quark.base.world.generator;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.WorldgenRandom;

/**
 * @author WireSegal
 * Created at 9:03 PM on 10/1/19.
 */
public interface IGenerator {
	int generate(int seedIncrement, long seed, GenerationStep.Decoration stage, WorldGenRegion worldIn, ChunkGenerator generator, WorldgenRandom rand, BlockPos pos);

	boolean canGenerate(ServerLevelAccessor world);
}
