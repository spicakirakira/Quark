package vazkii.quark.base.network.message;

import net.minecraftforge.network.NetworkEvent;
import vazkii.arl.network.IMessage;
import vazkii.quark.content.tweaks.module.LockRotationModule;
import vazkii.quark.content.tweaks.module.LockRotationModule.LockProfile;

import java.io.Serial;

public class SetLockProfileMessage implements IMessage {

	@Serial
	private static final long serialVersionUID = 1037317801540162515L;

	public LockProfile profile;

	public SetLockProfileMessage() { }

	public SetLockProfileMessage(LockProfile profile) {
		this.profile = profile;
	}

	@Override
	public boolean receive(NetworkEvent.Context context) {
		context.enqueueWork(() -> LockRotationModule.setProfile(context.getSender(), profile));
		return true;
	}

}
