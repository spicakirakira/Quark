package vazkii.quark.base.world;

import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.world.generator.IGenerator;

import javax.annotation.Nonnull;

public record WeightedGenerator(QuarkModule module,
								IGenerator generator,
								int weight) implements Comparable<WeightedGenerator> {

	@Override
	public int compareTo(@Nonnull WeightedGenerator o) {
		int diff = weight - o.weight;
		if (diff != 0)
			return diff;

		return hashCode() - o.hashCode();
	}

	@Override
	public int hashCode() {
		return generator.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this || (obj instanceof WeightedGenerator gen && gen.generator == generator);
	}

}
