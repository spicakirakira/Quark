package vazkii.quark.base.client.util;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Type;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.settings.KeyModifier;

import javax.annotation.Nonnull;
import java.util.function.BiPredicate;

@OnlyIn(Dist.CLIENT)
public class PredicatedKeyBinding extends KeyMapping {
	private final BiPredicate<KeyModifier, InputConstants.Key> allowed;

	public PredicatedKeyBinding(String description, Type type, int keyCode, String category, BiPredicate<KeyModifier, InputConstants.Key> allowed) {
		super(description, type, keyCode, category);
		this.allowed = allowed;
	}

	@Override
	public void setKeyModifierAndCode(@Nonnull KeyModifier keyModifier, @Nonnull InputConstants.Key keyCode) {
		if (allowed.test(keyModifier, keyCode))
			super.setKeyModifierAndCode(keyModifier, keyCode);
	}
}
