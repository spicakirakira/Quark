package vazkii.quark.mixin.accessor;


import net.minecraft.network.chat.ChatPreviewCache;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerGamePacketListenerImpl.class)
public interface AccessorServerGamePacketListenerImpl {
	@Accessor("chatPreviewCache")
	ChatPreviewCache quark$chatPreviewCache();
}
