package vazkii.quark.base.handler.advancement;

import javax.annotation.Nullable;

import com.google.common.base.Supplier;

import net.minecraft.resources.ResourceLocation;
import vazkii.quark.api.IAdvancementModifier;
import vazkii.quark.api.IMutableAdvancement;
import vazkii.quark.base.module.QuarkModule;

public abstract class AdvancementModifier implements IAdvancementModifier {

	public final QuarkModule module;
	private Supplier<Boolean> cond;
	
	protected AdvancementModifier(@Nullable QuarkModule module) {
		this.module = module;
	}

	@Override
	public AdvancementModifier setCondition(Supplier<Boolean> cond) {
		this.cond = cond;
		return this;
	}
	
	public boolean isActive() {
		return (module == null || module.enabled) && (cond == null || cond.get());
	}

}
