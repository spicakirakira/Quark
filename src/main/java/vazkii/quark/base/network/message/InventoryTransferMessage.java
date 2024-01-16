package vazkii.quark.base.network.message;

import net.minecraftforge.network.NetworkEvent;
import vazkii.arl.network.IMessage;
import vazkii.quark.base.handler.InventoryTransferHandler;

import java.io.Serial;

public class InventoryTransferMessage implements IMessage {

	@Serial
	private static final long serialVersionUID = 3825599549474465007L;

	public boolean smart, restock;

	public InventoryTransferMessage() { }

	public InventoryTransferMessage(boolean smart, boolean restock) {
		this.smart = smart;
		this.restock = restock;
	}

	@Override
	public boolean receive(NetworkEvent.Context context) {
		context.enqueueWork(() -> InventoryTransferHandler.transfer(context.getSender(), restock, smart));
		return true;
	}

}
