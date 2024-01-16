package vazkii.quark.base.module.config.type;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import vazkii.quark.base.module.config.Config;

public class StrictBiomeConfig extends AbstractConfigType implements IBiomeConfig {

	@Config(name = "Biomes")
	private List<String> biomeStrings;

	@Config
	private boolean isBlacklist;

	protected StrictBiomeConfig(boolean isBlacklist, String... biomes) {
		this.isBlacklist = isBlacklist;

		biomeStrings = new LinkedList<>();
		biomeStrings.addAll(Arrays.asList(biomes));
	}

	@Override
	public boolean canSpawn(Holder<Biome> res) {
		return res.unwrap().map(
				key -> biomeStrings.contains(key.location().toString()) != isBlacklist,
				unbound -> false
				);
	}

}
