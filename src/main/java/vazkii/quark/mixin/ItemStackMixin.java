package vazkii.quark.mixin;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Pair;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.quark.content.client.hax.PseudoAccessorItemStack;
import vazkii.quark.content.client.resources.AttributeSlot;
import vazkii.quark.content.client.tooltip.AttributeTooltips;
import vazkii.quark.content.management.module.ItemSharingModule;
import vazkii.quark.content.tools.module.AncientTomesModule;
import vazkii.quark.content.tweaks.module.GoldToolsHaveFortuneModule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(ItemStack.class)
public class ItemStackMixin implements PseudoAccessorItemStack {

	@Inject(method = "getDisplayName", at = @At("RETURN"), cancellable = true)
	private void getHoverName(CallbackInfoReturnable<Component> callbackInfoReturnable) {
		callbackInfoReturnable.setReturnValue(ItemSharingModule.createStackComponent((ItemStack) (Object) this, (MutableComponent) callbackInfoReturnable.getReturnValue()));
	}

	@Inject(method = "getRarity", at = @At("RETURN"), cancellable = true)
	private void getRarity(CallbackInfoReturnable<Rarity> callbackInfoReturnable) {
		callbackInfoReturnable.setReturnValue(AncientTomesModule.shiftRarity((ItemStack) (Object) this, callbackInfoReturnable.getReturnValue()));
	}

//	@ModifyExpressionValue(method = "getTooltipLines", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;hasTag()Z", ordinal = 0))
//	private boolean hasTagIfBaked(boolean hasTag) {
//		return hasTag || GoldToolsHaveFortuneModule.shouldShowEnchantments((ItemStack) (Object) this);
//	}

	@ModifyArg(method = "getTooltipLines", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;appendEnchantmentNames(Ljava/util/List;Lnet/minecraft/nbt/ListTag;)V"))
	private ListTag hideSmallerEnchantments(ListTag tag) {
		return GoldToolsHaveFortuneModule.hideSmallerEnchantments((ItemStack) (Object) this, tag);
	}

	@ModifyArg(method = "getTooltipLines", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;appendEnchantmentNames(Ljava/util/List;Lnet/minecraft/nbt/ListTag;)V"))
	private List<Component> appendEnchantmentNames(List<Component> components) {
		GoldToolsHaveFortuneModule.fakeEnchantmentTooltip((ItemStack) (Object) this, components);
		return components;
	}

	@Unique
	private Map<AttributeSlot, Multimap<Attribute, AttributeModifier>> capturedAttributes = new HashMap<>();

	@Unique
	private EquipmentSlot capturedSlot;

	@Override
	public Map<AttributeSlot, Multimap<Attribute, AttributeModifier>> quark$getCapturedAttributes() {
		return capturedAttributes;
	}

	@Override
	public void quark$capturePotionAttributes(List<Pair<Attribute, AttributeModifier>> attributes) {
		Multimap<Attribute, AttributeModifier> attributeContainer = LinkedHashMultimap.create();
		for (var pair : attributes) {
			attributeContainer.put(pair.getFirst(), pair.getSecond());
		}
		capturedAttributes.put(AttributeSlot.POTION, attributeContainer);
	}

	@Inject(method = "getTooltipLines", at = @At("HEAD"))
	private void clearCapturedTooltip(Player player, TooltipFlag flag, CallbackInfoReturnable<List<Component>> cir) {
		capturedAttributes = new HashMap<>();
	}

	@ModifyArg(method = "getTooltipLines", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getAttributeModifiers(Lnet/minecraft/world/entity/EquipmentSlot;)Lcom/google/common/collect/Multimap;"))
	private EquipmentSlot captureTooltipSlot(EquipmentSlot slot) {
		capturedSlot = slot;
		return slot;
	}

	@ModifyVariable(method = "getTooltipLines", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Multimap;isEmpty()Z", shift = At.Shift.BEFORE, remap = false))
	private Multimap<Attribute, AttributeModifier> overrideAttributeTooltips(Multimap<Attribute, AttributeModifier> attributes) {
		if (AttributeTooltips.shouldHideAttributes()) {
			capturedAttributes.put(AttributeSlot.fromCanonicalSlot(capturedSlot), LinkedHashMultimap.create(attributes));
			return ImmutableMultimap.of();
		}
		return attributes;
	}
}
