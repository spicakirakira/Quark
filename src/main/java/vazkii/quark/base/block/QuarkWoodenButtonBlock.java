package vazkii.quark.base.block;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.CreativeModeTab;
import vazkii.quark.base.module.QuarkModule;

import javax.annotation.Nonnull;

public class QuarkWoodenButtonBlock extends QuarkButtonBlock {

	public QuarkWoodenButtonBlock(String regname, QuarkModule module, Properties properties) {
		super(regname, module, CreativeModeTab.TAB_REDSTONE, properties);
	}

	@Nonnull
	@Override
	protected SoundEvent getSound(boolean powered) {
		return powered ? SoundEvents.WOODEN_BUTTON_CLICK_ON : SoundEvents.WOODEN_BUTTON_CLICK_OFF;
	}

	@Override
	public int getPressDuration() {
		return 30;
	}

}
