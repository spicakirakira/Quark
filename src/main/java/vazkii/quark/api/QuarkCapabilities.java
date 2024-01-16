package vazkii.quark.api;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class QuarkCapabilities {

	public static final Capability<ICustomSorting> SORTING = CapabilityManager.get(new CapabilityToken<>(){});

	public static final Capability<ITransferManager> TRANSFER = CapabilityManager.get(new CapabilityToken<>(){});

	public static final Capability<IPistonCallback> PISTON_CALLBACK = CapabilityManager.get(new CapabilityToken<>(){});

	public static final Capability<IMagnetTracker> MAGNET_TRACKER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

	public static final Capability<IRuneColorProvider> RUNE_COLOR = CapabilityManager.get(new CapabilityToken<>(){});
}
