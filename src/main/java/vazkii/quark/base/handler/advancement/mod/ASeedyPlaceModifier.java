package vazkii.quark.base.handler.advancement.mod;

import com.google.common.collect.ImmutableSet;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.BredAnimalsTrigger;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.EntityTypePredicate;
import net.minecraft.advancements.critereon.PlacedBlockTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.quark.api.IMutableAdvancement;
import vazkii.quark.base.handler.advancement.AdvancementModifier;
import vazkii.quark.base.handler.advancement.MutableAdvancement;
import vazkii.quark.base.module.QuarkModule;

import java.util.Set;

public class ASeedyPlaceModifier extends AdvancementModifier {

	private static final ResourceLocation TARGET = new ResourceLocation("husbandry/plant_seed");

	final Set<Block> seeds;

	public ASeedyPlaceModifier(QuarkModule module, Set<Block> seeds) {
		super(module);
		this.seeds = seeds;

	}

	@Override
	public Set<ResourceLocation> getTargets() {
		return ImmutableSet.of(TARGET);
	}

	@Override
	public boolean apply(ResourceLocation res, IMutableAdvancement adv) {
		for(var block : seeds) {
			Criterion criterion = new Criterion(PlacedBlockTrigger.TriggerInstance.placedBlock(block));
			
			String name = ForgeRegistries.BLOCKS.getKey(block).toString();
			adv.addOrCriterion(name, criterion);
		}
		
		return true;
	}

}
