package vazkii.quark.base.network;

import java.time.Instant;
import java.util.BitSet;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.LastSeenMessages;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.simple.SimpleChannel;
import vazkii.arl.network.IMessage;
import vazkii.arl.network.MessageSerializer;
import vazkii.arl.network.NetworkHandler;
import vazkii.quark.base.Quark;
import vazkii.quark.base.network.message.ChangeHotbarMessage;
import vazkii.quark.base.network.message.DoEmoteMessage;
import vazkii.quark.base.network.message.DoubleDoorMessage;
import vazkii.quark.base.network.message.EditSignMessage;
import vazkii.quark.base.network.message.HarvestMessage;
import vazkii.quark.base.network.message.InventoryTransferMessage;
import vazkii.quark.base.network.message.RequestEmoteMessage;
import vazkii.quark.base.network.message.ScrollOnBundleMessage;
import vazkii.quark.base.network.message.SetLockProfileMessage;
import vazkii.quark.base.network.message.ShareItemMessage;
import vazkii.quark.base.network.message.SortInventoryMessage;
import vazkii.quark.base.network.message.UpdateTridentMessage;
import vazkii.quark.base.network.message.experimental.PlaceVariantUpdateMessage;
import vazkii.quark.base.network.message.oddities.HandleBackpackMessage;
import vazkii.quark.base.network.message.oddities.MatrixEnchanterOperationMessage;
import vazkii.quark.base.network.message.oddities.ScrollCrateMessage;
import vazkii.quark.base.network.message.structural.C2SLoginFlag;
import vazkii.quark.base.network.message.structural.C2SUpdateFlag;
import vazkii.quark.base.network.message.structural.HandshakeMessage;
import vazkii.quark.base.network.message.structural.S2CLoginFlag;
import vazkii.quark.base.network.message.structural.S2CUpdateFlag;

public final class QuarkNetwork {

	private static final int PROTOCOL_VERSION = 2;

	private static NetworkHandler network;

	public static void setup() {
		MessageSerializer.mapHandlers(Instant.class, (buf, field) -> buf.readInstant(), (buf, field, instant) -> buf.writeInstant(instant));
		MessageSerializer.mapHandlers(MessageSignature.class, (buf, field) -> new MessageSignature(buf), (buf, field, signature) -> signature.write(buf));
		MessageSerializer.mapHandlers(LastSeenMessages.Update.class, (buf, field) -> new LastSeenMessages.Update(buf), (buf, field, update) -> update.write(buf));
		MessageSerializer.mapHandlers(BitSet.class, (buf, field) -> BitSet.valueOf(buf.readLongArray()), (buf, field, bitSet) -> buf.writeLongArray(bitSet.toLongArray()));

		network = new NetworkHandler(Quark.MOD_ID, PROTOCOL_VERSION);

		// Base Quark
		network.register(SortInventoryMessage.class, NetworkDirection.PLAY_TO_SERVER);
		network.register(InventoryTransferMessage.class, NetworkDirection.PLAY_TO_SERVER);
		network.register(DoubleDoorMessage.class, NetworkDirection.PLAY_TO_SERVER);
		network.register(HarvestMessage.class, NetworkDirection.PLAY_TO_SERVER);
		network.register(RequestEmoteMessage.class, NetworkDirection.PLAY_TO_SERVER);
		network.register(ChangeHotbarMessage.class, NetworkDirection.PLAY_TO_SERVER);
		network.register(SetLockProfileMessage.class, NetworkDirection.PLAY_TO_SERVER);
		network.register(ShareItemMessage.class, NetworkDirection.PLAY_TO_SERVER);
		network.register(ScrollOnBundleMessage.class, NetworkDirection.PLAY_TO_SERVER);

		// Oddities
		network.register(HandleBackpackMessage.class, NetworkDirection.PLAY_TO_SERVER);
		network.register(MatrixEnchanterOperationMessage.class, NetworkDirection.PLAY_TO_SERVER);
		network.register(ScrollCrateMessage.class, NetworkDirection.PLAY_TO_SERVER);

		// Experimental
		network.register(PlaceVariantUpdateMessage.class, NetworkDirection.PLAY_TO_SERVER);

		// Clientbound
		network.register(DoEmoteMessage.class, NetworkDirection.PLAY_TO_CLIENT);
		network.register(EditSignMessage.class, NetworkDirection.PLAY_TO_CLIENT);
		network.register(UpdateTridentMessage.class, NetworkDirection.PLAY_TO_CLIENT);

		// Flag Syncing
		network.register(S2CUpdateFlag.class, NetworkDirection.PLAY_TO_CLIENT);
		network.register(C2SUpdateFlag.class, NetworkDirection.PLAY_TO_SERVER);
		loginIndexedBuilder(S2CLoginFlag.class, 98, NetworkDirection.LOGIN_TO_CLIENT)
			.decoder(S2CLoginFlag::new)
			.buildLoginPacketList(S2CLoginFlag::generateRegistryPackets)
			.add();
		loginIndexedBuilder(C2SLoginFlag.class, 99, NetworkDirection.LOGIN_TO_SERVER)
			.decoder(C2SLoginFlag::new)
			.noResponse()
			.add();
	}

	private static <MSG extends HandshakeMessage> SimpleChannel.MessageBuilder<MSG> loginIndexedBuilder(Class<MSG> clazz, int id, NetworkDirection direction) {
		return network.channel.messageBuilder(clazz, id, direction)
			.loginIndex(HandshakeMessage::getLoginIndex, HandshakeMessage::setLoginIndex)
			.encoder(HandshakeMessage::encode)
			.consumerNetworkThread((msg, context) -> {
				return msg.consume(context.get(), network.channel::reply);
			});
	}

	public static void sendToPlayer(IMessage msg, ServerPlayer player) {
		if(network == null)
			return;
		
		network.sendToPlayer(msg, player);
	}

	@OnlyIn(Dist.CLIENT)
	public static void sendToServer(IMessage msg) {
		if(network == null || Minecraft.getInstance().getConnection() == null)
			return;
		
		network.sendToServer(msg);
	}

	public static void sendToPlayers(IMessage msg, Iterable<ServerPlayer> players) {
		if(network == null)
			return;
		
		for(ServerPlayer player : players)
			network.sendToPlayer(msg, player);
	}

	public static void sendToAllPlayers(IMessage msg, MinecraftServer server) {
		if(network == null)
			return;
		
		sendToPlayers(msg, server.getPlayerList().getPlayers());
	}

	public static Packet<?> toVanillaPacket(IMessage msg, NetworkDirection direction) {
		return network.channel.toVanillaPacket(msg, direction);
	}

}
