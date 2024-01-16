package vazkii.quark.mixin.accessor;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface AccessorLivingEntity {

	@Invoker("createLootContext")
	LootContext.Builder quark$createLootContext(boolean playerLoot, DamageSource source);

	@Accessor("lastHurtByPlayerTime")
	int quark$lastHurtByPlayerTime();

}
