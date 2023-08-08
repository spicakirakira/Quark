package vazkii.quark.content.building.module;

import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.util.CorundumColor;
import vazkii.quark.content.building.block.RainbowLampBlock;

@LoadModule(category = ModuleCategory.BUILDING)
public class RainbowLampsModule extends QuarkModule {

	@Config
	public static int lightLevel = 15;

	@Config(description = "Whether Rainbow Lamps should be made from and themed on Corundum if that module is enabled.", flag = "rainbow_lamp_corundum")
	public static boolean useCorundum = true;

	public static boolean isCorundum() {
		return ModuleLoader.INSTANCE.isModuleEnabled(RainbowLampsModule.class) && useCorundum;
	}

	@Override
	public void register() {
		for(CorundumColor color : CorundumColor.values())
			new RainbowLampBlock(color.name + "_crystal_lamp", color.beaconColor, this, color.materialColor);
	}
}
