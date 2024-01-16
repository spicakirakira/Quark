package vazkii.quark.base.handler.advancement.mod;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.FilledBucketTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.quark.api.IMutableAdvancement;
import vazkii.quark.base.handler.advancement.AdvancementModifier;
import vazkii.quark.base.handler.advancement.MutableAdvancement;
import vazkii.quark.base.module.QuarkModule;

import java.util.Set;

public class TacticalFishingModifier extends AdvancementModifier {

    private static final ResourceLocation TARGET = new ResourceLocation("husbandry/tactical_fishing");

    final Set<BucketItem> bucketItems;

    public TacticalFishingModifier(QuarkModule module, Set<BucketItem> buckets) {
        super(module);
        this.bucketItems = buckets;
        Preconditions.checkArgument(!buckets.isEmpty(), "Advancement modifier list cant be empty");
    }

    @Override
    public Set<ResourceLocation> getTargets() {
        return ImmutableSet.of(TARGET);
    }

    @Override
    public boolean apply(ResourceLocation res, IMutableAdvancement adv) {

        ItemLike[] array = bucketItems.toArray(ItemLike[]::new);
        Criterion criterion = new Criterion(FilledBucketTrigger.
                TriggerInstance.filledBucket(ItemPredicate.Builder.item()
                        .of(array).build()));

        String name = ForgeRegistries.ITEMS.getKey(array[0].asItem()).toString();
        adv.addOrCriterion(name, criterion);

        return true;
    }

}
