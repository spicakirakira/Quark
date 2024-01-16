package vazkii.quark.base.util;

import com.mojang.authlib.GameProfile;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;

@Deprecated(forRemoval = true)
public class MovableFakePlayer extends FakePlayer {

	public MovableFakePlayer(ServerLevel world, GameProfile name) {
		super(world, name);
	}

	@Override
	public Vec3 position() {
		return new Vec3(getX(), getY(), getZ());
	}
	
	@Override
	public BlockPos blockPosition() {
		return new BlockPos((int) getX(), (int) getY(), (int) getZ());
	}
	
}
