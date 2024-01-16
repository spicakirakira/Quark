package vazkii.quark.content.world.config;

import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.config.type.AbstractConfigType;
import vazkii.quark.base.module.config.type.DimensionConfig;
import vazkii.quark.base.module.config.type.OrePocketConfig;

public class StoneTypeConfig extends AbstractConfigType {

	@Config public DimensionConfig dimensions;
	@Config public OrePocketConfig oregenLower = new OrePocketConfig(0, 60, 64, 2.0);
	@Config public OrePocketConfig oregenUpper = new OrePocketConfig(64, 128, 64, 0.1666666);

	public StoneTypeConfig(DimensionConfig config) {
		dimensions = config;
	}
	
	public StoneTypeConfig() {
		this(DimensionConfig.overworld(false));
	}

}
