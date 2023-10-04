package vazkii.quark.base.network.message.structural;

import net.minecraftforge.network.NetworkEvent;
import vazkii.arl.network.IMessage;
import vazkii.quark.base.module.sync.SyncedFlagHandler;

import java.io.Serial;
import java.util.BitSet;

public class C2SUpdateFlag implements IMessage {

	@Serial
	private static final long serialVersionUID = 7483741039149504284L;

	public BitSet flags;

	@Override
	public boolean receive(NetworkEvent.Context context) {
		SyncedFlagHandler.receiveFlagInfoFromPlayer(context.getSender(), flags);
		return true;
	}

	public C2SUpdateFlag() {
		// NO-OP
	}

	private C2SUpdateFlag(BitSet flags) {
		this.flags = flags;
	}

	public static C2SUpdateFlag createPacket() {
		return new C2SUpdateFlag(SyncedFlagHandler.compileFlagInfo());
	}
}
