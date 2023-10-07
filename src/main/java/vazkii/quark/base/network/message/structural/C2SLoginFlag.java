package vazkii.quark.base.network.message.structural;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import vazkii.quark.base.module.sync.SyncedFlagHandler;

import java.util.BitSet;
import java.util.function.BiConsumer;

public class C2SLoginFlag extends HandshakeMessage {

	public BitSet flags;

	public C2SLoginFlag() {
		flags = SyncedFlagHandler.compileFlagInfo();
	}

	public C2SLoginFlag(FriendlyByteBuf buf) {
		this.flags = BitSet.valueOf(buf.readLongArray());
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeLongArray(flags.toLongArray());
	}

	@Override
	public boolean consume(NetworkEvent.Context context, BiConsumer<HandshakeMessage, NetworkEvent.Context> reply) {
		SyncedFlagHandler.receiveFlagInfoFromPlayer(context.getSender(), flags);
		return true;
	}

}
