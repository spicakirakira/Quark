package vazkii.quark.content.automation.base;

import java.util.Locale;

import net.minecraft.util.StringRepresentable;

import javax.annotation.Nonnull;

/**
 * @author WireSegal
 * Created at 10:12 AM on 8/26/19.
 */
public enum RandomizerPowerState implements StringRepresentable {
	OFF, LEFT, RIGHT;


	@Nonnull
	@Override
	public String getSerializedName() {
		return name().toLowerCase(Locale.ROOT);
	}
}
