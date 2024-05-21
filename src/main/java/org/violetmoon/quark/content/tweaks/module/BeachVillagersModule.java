package org.violetmoon.quark.content.tweaks.module;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.level.biome.Biomes;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.load.ZRegister;
import org.violetmoon.zeta.module.ZetaLoadModule;
import org.violetmoon.zeta.module.ZetaModule;

import java.util.HashMap;

@ZetaLoadModule(category = "tweaks")
public class BeachVillagersModule extends ZetaModule {

    // need it here so I can reference it early in the trade mixin
    public static VillagerType beach = new VillagerType("beach");

    @LoadEvent
    public final void register(ZRegister event) {

        event.getRegistry().register(beach, "beach", Registries.VILLAGER_TYPE);
        var map = new HashMap<>(VillagerType.BY_BIOME);
        map.put(Biomes.BEACH, beach);
        VillagerType.BY_BIOME = map;
    }
}
