package vazkii.quark.content.automation.module;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import vazkii.quark.base.module.Hint;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.content.automation.block.RedstoneRandomizerBlock;

/**
 * @author WireSegal
 * Created at 10:34 AM on 8/26/19.
 */
@LoadModule(category = ModuleCategory.AUTOMATION)
public class RedstoneRandomizerModule extends QuarkModule {

	@Hint Block redstone_randomizer;
	
	@Override
	public void register() {
		redstone_randomizer = new RedstoneRandomizerBlock("redstone_randomizer", this, CreativeModeTab.TAB_REDSTONE, Block.Properties.of(Material.DECORATION).strength(0).sound(SoundType.WOOD));
	}
}
