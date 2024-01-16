package vazkii.quark.base.module;

import net.minecraftforge.fml.loading.FMLEnvironment;

public enum SubscriptionTarget {

	BOTH_SIDES(true, true),
	CLIENT_ONLY(true, false),
	SERVER_ONLY(false, true),
	NONE(false, false);

	SubscriptionTarget(boolean client, boolean server) {
		this.client = client;
		this.server = server;
	}

	private final boolean client;
	private final boolean server;

	public boolean shouldSubscribe() {
		return FMLEnvironment.dist.isClient() ? client : server;
	}

	public static SubscriptionTarget fromString(String s) {
		for(SubscriptionTarget target : values())
			if(target.name().equals(s))
				return target;

		return null;
	}


}
