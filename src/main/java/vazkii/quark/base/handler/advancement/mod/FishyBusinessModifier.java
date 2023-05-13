package vazkii.quark.base.handler.advancement.mod;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.FishingRodHookedTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.quark.api.IMutableAdvancement;
import vazkii.quark.base.handler.advancement.AdvancementModifier;
import vazkii.quark.base.handler.advancement.MutableAdvancement;
import vazkii.quark.base.module.QuarkModule;

import java.util.Set;

public class FishyBusinessModifier extends AdvancementModifier {

    private static final ResourceLocation TARGET = new ResourceLocation("husbandry/fishy_business");

    final Set<ItemLike> fishes;

    public FishyBusinessModifier(QuarkModule module, Set<ItemLike> fishes) {
        super(module);
        this.fishes = fishes;
        Preconditions.checkArgument(!fishes.isEmpty(), "Advancement modifier list cant be empty");
    }

    @Override
    public Set<ResourceLocation> getTargets() {
        return ImmutableSet.of(TARGET);
    }

    @Override
    public boolean apply(ResourceLocation res, IMutableAdvancement adv) {

        ItemLike[] array = fishes.toArray(ItemLike[]::new);
        Criterion criterion = new Criterion(FishingRodHookedTrigger.
                TriggerInstance.fishedItem(
                        ItemPredicate.ANY,
                        EntityPredicate.ANY,
                        ItemPredicate.Builder.item().of(array).build()));

        String name = ForgeRegistries.ITEMS.getKey(array[0].asItem()).toString();
        adv.addOrCriterion(name, criterion);

        return true;
    }

}
