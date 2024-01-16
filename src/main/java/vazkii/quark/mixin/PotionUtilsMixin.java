package vazkii.quark.mixin;

import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import vazkii.quark.content.client.tooltip.AttributeTooltips;
import vazkii.quark.content.client.hax.PseudoAccessorItemStack;

import java.util.Collections;
import java.util.List;

@Mixin(value = PotionUtils.class)
public class PotionUtilsMixin {

	@ModifyVariable(method = "addPotionTooltip", at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z", ordinal = 1, shift = At.Shift.BEFORE), ordinal = 2)
	private static List<Pair<Attribute, AttributeModifier>> overrideAttributeTooltips(List<Pair<Attribute, AttributeModifier>> attributes, ItemStack stack) {
		if (AttributeTooltips.shouldHideAttributes()) {
			((PseudoAccessorItemStack) (Object) stack).quark$capturePotionAttributes(attributes);
			return Collections.emptyList();
		}
		return attributes;
	}
}
