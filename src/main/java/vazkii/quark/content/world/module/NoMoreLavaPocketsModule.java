package vazkii.quark.content.world.module;

import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.levelgen.feature.configurations.SpringConfiguration;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;

@LoadModule(category = ModuleCategory.WORLD)
public class NoMoreLavaPocketsModule extends QuarkModule {

	private static boolean staticEnabled;

	@Override
	public void configChanged() {
		staticEnabled = enabled;
	}

	public static boolean shouldDisable(SpringConfiguration configuration) {
		return staticEnabled && !configuration.requiresBlockBelow && configuration.state.is(FluidTags.LAVA);
	}
}
