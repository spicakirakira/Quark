package vazkii.quark.api;

import java.util.Set;

import com.google.common.base.Supplier;

import net.minecraft.resources.ResourceLocation;

public interface IAdvancementModifier {

	Set<ResourceLocation> getTargets();
	boolean apply(ResourceLocation res, IMutableAdvancement adv);


	default IAdvancementModifier setCondition(Supplier<Boolean> cond){
		return this;
	}

	default boolean isActive(){
		return true;
	}

}
