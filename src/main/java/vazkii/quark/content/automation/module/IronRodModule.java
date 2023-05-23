package vazkii.quark.content.automation.module;

import net.minecraft.world.level.block.Block;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.hint.Hint;
import vazkii.quark.content.automation.block.IronRodBlock;

@LoadModule(category = ModuleCategory.AUTOMATION)
public class IronRodModule extends QuarkModule {
	
	@Config(flag = "iron_rod_pre_end") 
	public static boolean usePreEndRecipe = false;
	
	@Hint public static Block iron_rod;
	
	@Override
	public void register() {
		iron_rod = new IronRodBlock(this);
	}
	
}
