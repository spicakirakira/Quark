package vazkii.quark.base.network.message.structural;

import net.minecraftforge.network.NetworkEvent;
import vazkii.arl.network.IMessage;
import vazkii.quark.base.module.sync.SyncedFlagHandler;

import java.io.Serial;
import java.util.BitSet;

public class S2CUpdateFlag implements IMessage {

	@Serial
	private static final long serialVersionUID = 2346906475290526858L;

	public BitSet flags;

	@Override
	public boolean receive(NetworkEvent.Context context) {
		SyncedFlagHandler.receiveFlagInfoFromServer(flags);
		return true;
	}

	public S2CUpdateFlag() {
		// NO-OP
	}

	private S2CUpdateFlag(BitSet flags) {
		this.flags = flags;
	}

	public static S2CUpdateFlag createPacket() {
		return new S2CUpdateFlag(SyncedFlagHandler.compileFlagInfo());
	}
}
