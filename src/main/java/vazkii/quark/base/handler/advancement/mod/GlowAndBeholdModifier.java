package vazkii.quark.base.handler.advancement.mod;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.ItemInteractWithBlockTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.quark.addons.oddities.inventory.slot.BackpackSlot;
import vazkii.quark.api.IMutableAdvancement;
import vazkii.quark.base.handler.advancement.AdvancementModifier;
import vazkii.quark.base.module.QuarkModule;

import java.util.Set;

public class GlowAndBeholdModifier extends AdvancementModifier {

    private static final ResourceLocation TARGET = new ResourceLocation("husbandry/make_a_sign_glow");

    final Set<Block> blocks;

    public GlowAndBeholdModifier(QuarkModule module, Set<Block> buckets) {
        super(module);
        this.blocks = buckets;
        Preconditions.checkArgument(!blocks.isEmpty(), "Advancement modifier list cant be empty");
    }

    @Override
    public Set<ResourceLocation> getTargets() {
        return ImmutableSet.of(TARGET);
    }

    @Override
    public boolean apply(ResourceLocation res, IMutableAdvancement adv) {

        Block[] array = blocks.toArray(Block[]::new);
        Criterion criterion = new Criterion(ItemInteractWithBlockTrigger.
                TriggerInstance.itemUsedOnBlock(
                        LocationPredicate.Builder.location().setBlock(
                                BlockPredicate.Builder.block()
                                        .of(array).build()),
                        ItemPredicate.Builder.item().of(Items.GLOW_INK_SAC)));

        String name = ForgeRegistries.BLOCKS.getKey(array[0]).toString();
        adv.addOrCriterion(name, criterion);

        return true;
    }

}
