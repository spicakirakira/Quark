package vazkii.quark.base.module.config;

import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import vazkii.quark.base.Quark;
import vazkii.quark.base.handler.GeneralConfig;

/**
 * @author WireSegal
 * Created at 1:23 PM on 8/24/19.
 */
public record FlagAdvancementCondition(ConfigFlagManager manager, String flag,
								  ResourceLocation loc) implements ICondition {


	@Override
	public ResourceLocation getID() {
		return loc;
	}

	@Override
	public boolean test(IContext context) {
		if (flag.contains("%"))
			throw new RuntimeException("Illegal flag: " + flag);

		if (!manager.isValidFlag(flag))
			Quark.LOG.warn("Non-existant flag " + flag + " being used");

		return GeneralConfig.enableQuarkAdvancements && manager.getFlag(flag);
	}

	public static class Serializer implements IConditionSerializer<FlagAdvancementCondition> {
		private final ConfigFlagManager manager;
		private final ResourceLocation location;

		public Serializer(ConfigFlagManager manager, ResourceLocation location) {
			this.manager = manager;
			this.location = location;
		}

		@Override
		public void write(JsonObject json, FlagAdvancementCondition value) {
			json.addProperty("flag", value.flag);
		}

		@Override
		public FlagAdvancementCondition read(JsonObject json) {
			return new FlagAdvancementCondition(manager, json.getAsJsonPrimitive("flag").getAsString(), location);
		}

		@Override
		public ResourceLocation getID() {
			return location;
		}
	}
}
