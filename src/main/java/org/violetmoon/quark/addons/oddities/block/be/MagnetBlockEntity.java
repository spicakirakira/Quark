package org.violetmoon.quark.addons.oddities.block.be;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.violetmoon.quark.addons.oddities.block.MagnetBlock;
import org.violetmoon.quark.addons.oddities.magnetsystem.MagnetSystem;
import org.violetmoon.quark.addons.oddities.module.MagnetsModule;
import org.violetmoon.quark.api.IMagneticEntity;
import org.violetmoon.zeta.api.ICollateralMover;

public class MagnetBlockEntity extends BlockEntity {

    public MagnetBlockEntity(BlockPos pos, BlockState state) {
        super(MagnetsModule.magnetType, pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, MagnetBlockEntity be) {
        boolean powered = state.getValue(MagnetBlock.POWERED);

        if (powered) {
            Direction dir = state.getValue(MagnetBlock.FACING);
            int power = level.getBestNeighborSignal(pos);
            be.magnetize(state, dir, dir, power);
            be.magnetize(state, dir.getOpposite(), dir, power);
        }
    }

    private void magnetize(BlockState state, Direction dir, Direction moveDir, int power) {
        if (level == null)
            return;

        double magnitude = (dir == moveDir ? 1 : -1);

        double particleMotion = 0.06 * magnitude;
        double particleChance = 0.2;
        double xOff = dir.getStepX() * particleMotion;
        double yOff = dir.getStepY() * particleMotion;
        double zOff = dir.getStepZ() * particleMotion;

        double particleOffset = moveDir.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1 : -1;

        int i = 1;
        for (; i <= power; i++) {
            BlockPos targetPos = worldPosition.relative(dir, i);
            BlockState targetState = level.getBlockState(targetPos);

            if (targetState.getBlock() == MagnetsModule.magnetized_block) break;

            if (!level.isClientSide && targetState.getBlock() != Blocks.MOVING_PISTON) {
                var reaction = MagnetSystem.getPushAction(this, targetPos, targetState, moveDir);
                if (reaction == ICollateralMover.MoveResult.MOVE || reaction == ICollateralMover.MoveResult.BREAK) {
                    MagnetSystem.applyForce(level, targetPos, power - i + 1, dir == moveDir, moveDir, i, worldPosition);
                } else if (reaction == ICollateralMover.MoveResult.PREVENT) break;
            }

            if (!canBeReplacedByMovingMagnet(targetState)) break;

            if (level.isClientSide && !state.getValue(MagnetBlock.WAXED) && level.random.nextFloat() <= particleChance) {
                RandomSource ran = level.random;
                double x = targetPos.getX() + getParticlePos(xOff, ran, particleOffset);
                double y = targetPos.getY() + getParticlePos(yOff, ran, particleOffset);
                double z = targetPos.getZ() + getParticlePos(zOff, ran, particleOffset);
                var p = dir == moveDir ? MagnetsModule.repulsorParticle : MagnetsModule.attractorParticle;
                level.addParticle(p, x, y, z, xOff, yOff, zOff);
            }
        }

        //TODO: move this into magnet system. although might not be needed as there it only serves since directions must be discrete
        if (!level.isClientSide && MagnetsModule.affectEntities && i > 1) {

            var entities = level.getEntities((Entity) null, new AABB(worldPosition)
                            .expandTowards(new Vec3(dir.step().mul(i))), this::canPullEntity);
            for (Entity e : entities) {
                double distanceFromMagnetSq = e.distanceToSqr(worldPosition.getCenter());
                double invSquared = 1 / distanceFromMagnetSq;
                // magic number chosen. around 1 block hover height for iron golems
                Vec3 vec = new Vec3(dir.step().mul((float) (invSquared * magnitude * MagnetsModule.entitiesPullForce)));
                if (e instanceof IMagneticEntity me) {
                    me.moveByMagnet(e, vec, this);
                } else {
                    e.push(vec.x(), vec.y(), vec.z());
                    if (e instanceof Player player) {
                        //should probably send a packet here actually
                        player.hurtMarked = true;
                    }
                    if(e instanceof FallingBlockEntity fb){
                        fb.time--;
                        fb.hurtMarked = true;
                        //hack.
                    }
                }
            }
        }
    }

    private boolean canPullEntity(Entity e){
        if (e instanceof IMagneticEntity) return true;
        if (e.getType().is(MagnetsModule.magneticEntities)) return true;

        if (e instanceof ItemEntity ie){
            return MagnetSystem.isItemMagnetic(ie.getItem().getItem());
        }
        if (e instanceof FallingBlockEntity fb){
            return MagnetSystem.isBlockMagnetic(fb.getBlockState());
        }

        if (MagnetsModule.affectsArmor){
            for (var armor : e.getArmorSlots()){
                if (MagnetSystem.isItemMagnetic(armor.getItem())) return true;
            }
        }
        return false;
    }

    private boolean canBeReplacedByMovingMagnet(BlockState targetState) {
        return targetState.isAir() || targetState.getPistonPushReaction() == PushReaction.DESTROY;
    }

    private double getParticlePos(double offset, RandomSource ran, double magnitude) {
        return (offset == 0 ? 0.5f + (ran.nextFloat() + ran.nextFloat() - 1) / 2f : (0.5f + magnitude * (ran.nextFloat() - 1.25)));
    }
}
