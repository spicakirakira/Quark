package vazkii.quark.content.tools.module;

import net.minecraft.world.item.Item;
import vazkii.quark.base.module.Hint;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.content.tools.item.TrowelItem;

@LoadModule(category = ModuleCategory.TOOLS)
public class TrowelModule extends QuarkModule {

	@Config(name = "Trowel Max Durability",
			description = "Amount of blocks placed is this value + 1.\nSet to 0 to make the Trowel unbreakable")
	@Config.Min(0)
	public static int maxDamage = 0;

	@Hint Item trowel;
	
	@Override
	public void register() {
		trowel = new TrowelItem(this);
	}
	
	
}
