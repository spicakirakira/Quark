package vazkii.quark.base.item;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.ItemStack;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.base.module.QuarkModule;

import javax.annotation.Nonnull;
import java.util.function.BooleanSupplier;

public class QuarkDoubleHighBlockItem extends DoubleHighBlockItem implements IQuarkItem {

	private final QuarkModule module;

	private BooleanSupplier enabledSupplier = () -> true;

	public QuarkDoubleHighBlockItem(IQuarkBlock baseBlock, Properties props) {
		super(baseBlock.getBlock(), props);

		this.module = baseBlock.getModule();
	}

	@Override
	public void fillItemCategory(@Nonnull CreativeModeTab group, @Nonnull NonNullList<ItemStack> items) {
		if(isEnabled() || group == CreativeModeTab.TAB_SEARCH)
			super.fillItemCategory(group, items);
	}

	@Override
	public QuarkDoubleHighBlockItem setCondition(BooleanSupplier enabledSupplier) {
		this.enabledSupplier = enabledSupplier;
		return this;
	}

	@Override
	public QuarkModule getModule() {
		return module;
	}

	@Override
	public boolean doesConditionApply() {
		return enabledSupplier.getAsBoolean();
	}

}
