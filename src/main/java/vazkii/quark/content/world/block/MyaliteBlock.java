package vazkii.quark.content.world.block;

import net.minecraft.world.item.CreativeModeTab;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.module.QuarkModule;

public class MyaliteBlock extends QuarkBlock implements IMyaliteColorProvider {

	public MyaliteBlock(String regname, QuarkModule module, CreativeModeTab creativeTab, Properties properties) {
		super(regname, module, creativeTab, properties);
	}

}
