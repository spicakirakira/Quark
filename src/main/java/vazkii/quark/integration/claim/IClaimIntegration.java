package vazkii.quark.integration.claim;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;

import javax.annotation.Nonnull;

public interface IClaimIntegration {

    IClaimIntegration INSTANCE = Util.make(() -> {
        if (ModList.get().isLoaded("flan")) {
            return new FlanIntegration();
        }
        return new IClaimIntegration() {
        }; //NO OP
    });

    default boolean canBreak(@Nonnull Player player, @Nonnull BlockPos pos) {
        return true;
    }

    default boolean canPlace(@Nonnull Player player, @Nonnull BlockPos pos) {
        return true;
    }

    default boolean canReplace(@Nonnull Player player, @Nonnull BlockPos pos) {
        return true;
    }

    default boolean canAttack(@Nonnull Player player, @Nonnull Entity victim) {
        return true;
    }

    default boolean canInteract(@Nonnull Player player, @Nonnull BlockPos targetPos) {
        return true;
    }

}
