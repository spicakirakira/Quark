package vazkii.quark.base.client.util;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Type;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.settings.KeyModifier;

import javax.annotation.Nonnull;
import java.util.function.BiPredicate;

@OnlyIn(Dist.CLIENT)
public class SortedPredicatedKeyBinding extends SortedKeyBinding {
	private final BiPredicate<KeyModifier, InputConstants.Key> allowed;

	public SortedPredicatedKeyBinding(String description, Type type, int keyCode, String category, int priority, BiPredicate<KeyModifier, InputConstants.Key> allowed) {
		super(description, type, keyCode, category, priority);
		this.allowed = allowed;
	}

	@Override
	public void setKeyModifierAndCode(@Nonnull KeyModifier keyModifier, @Nonnull InputConstants.Key keyCode) {
		if (allowed.test(keyModifier, keyCode))
			super.setKeyModifierAndCode(keyModifier, keyCode);
	}
}
