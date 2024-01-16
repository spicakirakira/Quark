package vazkii.quark.base.network.message;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import vazkii.arl.network.IMessage;
import vazkii.quark.base.handler.ContributorRewardHandler;
import vazkii.quark.base.network.QuarkNetwork;

import java.io.Serial;

public class RequestEmoteMessage implements IMessage {

	@Serial
	private static final long serialVersionUID = -8569122937119059414L;

	public String emote;

	public RequestEmoteMessage() { }

	public RequestEmoteMessage(String emote) {
		this.emote = emote;
	}

	@Override
	public boolean receive(NetworkEvent.Context context) {
		ServerPlayer player = context.getSender();
		if (player != null && player.server != null)
			context.enqueueWork(() -> QuarkNetwork.sendToAllPlayers(
					new DoEmoteMessage(emote, player.getUUID(), ContributorRewardHandler.getTier(player)),
					player.server));
		return true;
	}

}
