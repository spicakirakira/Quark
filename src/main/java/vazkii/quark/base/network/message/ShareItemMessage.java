package vazkii.quark.base.network.message;

import java.io.Serial;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;
import vazkii.arl.network.IMessage;
import vazkii.quark.content.management.module.ItemSharingModule;

public class ShareItemMessage implements IMessage {

	@Serial
	private static final long serialVersionUID = 2204175080232208578L;

	public int containerId;
	public int slot;
	
	public ShareItemMessage() { }
	
	public ShareItemMessage(int slot, int containerId) {
		this.slot = slot;
		this.containerId = containerId;
	}
	
	@Override
	public boolean receive(Context context) {
		ServerPlayer player = context.getSender();
		if (player != null && player.server != null)
			context.enqueueWork(() -> ItemSharingModule.shareItem(player, slot, containerId));
		
		return true;
	}

}
