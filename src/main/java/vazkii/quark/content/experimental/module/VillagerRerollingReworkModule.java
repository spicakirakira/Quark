package vazkii.quark.content.experimental.module;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraftforge.event.entity.living.LivingConversionEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.content.experimental.hax.PseudoAccessorMerchantOffer;
import vazkii.quark.mixin.accessor.AccessorMerchantOffer;

@LoadModule(category = ModuleCategory.EXPERIMENTAL, enabledByDefault = false, hasSubscriptions = true)
public class VillagerRerollingReworkModule extends QuarkModule {

	public static final String TAG_VILLAGER_SEED = "quark:MerchantInitialSeed";
	public static final String TAG_TRADE_TIER = "quark:tier";

	public static boolean staticEnabled;

	@Config(description = "If enabled, the first two trades a villager generates for a profession will always be the same for a given villager.\n" +
		"This prevents repeatedly placing down a job site block to reroll the villager's trades.")
	public static boolean seedInitialVillagerTrades = true;

	@Config(description = """
		Set to 0 to disable the chance to reroll trades when restocking.
		It's possible for a trade to not restock even when the chance is 1. This happens when the rerolled trade is one the villager already has.
		This chance only guarantees a reroll will be attempted.""")
	@Config.Min(0)
	@Config.Max(1)
	public static double chanceToRerollWhenRestocking = 0.25;

	@Config(description = "Set to 0 to disable the chance to reroll trades when restocking. Set to -1 to allow unlimited rerolling.\n" +
		"Trades earlier in the list will restock first.")
	public static int maximumRestocksPerDay = 3;

	@Override
	public void configChanged() {
		staticEnabled = enabled;
	}

	@SubscribeEvent
	public void assignSeedIfUnassigned(LivingEvent.LivingTickEvent event) {
		LivingEntity entity = event.getEntity();
		if (canUseSeededRandom(entity)) {
			CompoundTag persistent = entity.getPersistentData();

			if (!persistent.contains(TAG_VILLAGER_SEED, Tag.TAG_LONG))
				persistent.putLong(TAG_VILLAGER_SEED, entity.getRandom().nextLong());
		}
	}

	@SubscribeEvent
	public void keepSeedOnConversion(LivingConversionEvent.Post event) {
		LivingEntity original = event.getEntity();
		LivingEntity outcome = event.getOutcome();
		if (canUseSeededRandom(original) || canUseSeededRandom(outcome)) {
			CompoundTag persistent = original.getPersistentData();
			if (persistent.contains(TAG_VILLAGER_SEED, Tag.TAG_LONG))
				outcome.getPersistentData().putLong(TAG_VILLAGER_SEED, persistent.getLong(TAG_VILLAGER_SEED));
		}
	}

	public static void attemptToReroll(Villager villager) {
		if (!staticEnabled || maximumRestocksPerDay == 0 || chanceToRerollWhenRestocking == 0)
			return;

		int restocks = 0;

		MerchantOffers offers = villager.getOffers();

		for (int i = 0; i < offers.size(); i++) {
			MerchantOffer offer = offers.get(i);
			if (offer.isOutOfStock()) {
				MerchantOffer rerolled = attemptToReroll(villager, offer);

				if (rerolled != null) {
					boolean foundEquivalent = false; // We avoid duplicate trades...
					boolean rerollingInPlace = false; // But it's okay to reroll a trade into a different variation of itself.
					for (MerchantOffer otherOffer : offers) {

						if (ItemStack.isSame(otherOffer.getBaseCostA(), rerolled.getBaseCostA()) &&
							ItemStack.isSame(otherOffer.getCostB(), rerolled.getCostB()) &&
							ItemStack.isSame(otherOffer.getResult(), rerolled.getResult())) {
							if (otherOffer == offer)
								rerollingInPlace = true;
							else
								foundEquivalent = true;
							break;
						}
					}

					if (!foundEquivalent) {
						rerolled.addToSpecialPriceDiff(offer.getSpecialPriceDiff());
						((AccessorMerchantOffer) rerolled).quark$setRewardExp(rerolled.shouldRewardExp() && offer.shouldRewardExp());

						if (rerollingInPlace) {
							for (int use = 0; use < offer.getUses(); use++)
								rerolled.increaseUses();
							((AccessorMerchantOffer) rerolled).quark$setDemand(offer.getDemand());
						} else
							restocks++;

						offers.set(i, rerolled);

						if (restocks >= maximumRestocksPerDay && maximumRestocksPerDay > 0)
							break;
					}
				}
			}
		}
	}

	public static MerchantOffer attemptToReroll(Villager villager, MerchantOffer original) {
		if (((PseudoAccessorMerchantOffer) original).quark$getTier() > VillagerData.MAX_VILLAGER_LEVEL)
			return null;

		if (villager.getRandom().nextDouble() >= chanceToRerollWhenRestocking)
			return null;

		int tier = ((PseudoAccessorMerchantOffer) original).quark$getTier();
		if (tier >= 0 && tier <= VillagerData.MAX_VILLAGER_LEVEL) {
			VillagerData data = villager.getVillagerData();
			var trades = VillagerTrades.TRADES.get(data.getProfession());
			if (trades != null && !trades.isEmpty()) {
				var listings = trades.get(tier);
				if (listings != null && listings.length > 0) {
					var listing = listings[villager.getRandom().nextInt(listings.length)];
					MerchantOffer newOffer = listing.getOffer(villager, villager.getRandom());
					if (newOffer != null) {
						((PseudoAccessorMerchantOffer) newOffer).quark$setTier(tier);
						return newOffer;
					}
				}
			}
		}

		return null;
	}

	public static boolean canUseSeededRandom(LivingEntity villager) {
		return staticEnabled && seedInitialVillagerTrades && villager instanceof VillagerDataHolder;
	}

	public static boolean shouldUseSeededRandom(LivingEntity villager, MerchantOffers offers) {
		return canUseSeededRandom(villager) && offers.isEmpty();
	}

	public static RandomSource seededRandomForVillager(AbstractVillager villager) {
		long seed = villager.getPersistentData().getLong(TAG_VILLAGER_SEED);

		return RandomSource.create(seed);
	}
}
