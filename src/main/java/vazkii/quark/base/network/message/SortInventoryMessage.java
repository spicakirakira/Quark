package vazkii.quark.base.network.message;

import net.minecraftforge.network.NetworkEvent;
import vazkii.arl.network.IMessage;
import vazkii.quark.base.handler.SortingHandler;

import java.io.Serial;

public class SortInventoryMessage implements IMessage {

	@Serial
	private static final long serialVersionUID = -4340505435110793951L;

	public boolean forcePlayer;

	public SortInventoryMessage() { }

	public SortInventoryMessage(boolean forcePlayer) {
		this.forcePlayer = forcePlayer;
	}

	@Override
	public boolean receive(NetworkEvent.Context context) {
		context.enqueueWork(() -> SortingHandler.sortInventory(context.getSender(), forcePlayer));
		return true;
	}

}
