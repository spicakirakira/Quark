package vazkii.quark.addons.oddities.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;

import java.util.List;
import java.util.Locale;

public record TinyPotatoInfo(int runeColor, boolean enchanted, String name) {

	private static final List<String> RAINBOW_NAMES = List.of("gay homosexual", "rainbow", "lgbt", "lgbtq", "lgbtq+", "gay");
	private static final List<String> ENCHANTMENT_NAMES = List.of("enchanted", "glowy", "shiny", "gay");

	private static boolean matches(String name, String match) {
		return name.equals(match) || name.startsWith(match + " ");
	}

	private static String removeFromFront(String name, String match) {
		return name.substring(match.length()).trim();
	}

	public static TinyPotatoInfo fromComponent(Component component) {
		return fromString(component.getString());
	}

	public static TinyPotatoInfo fromString(String string) {
		string = ChatFormatting.stripFormatting(string);

		if (string == null)
			return new TinyPotatoInfo(-1, false, "");

		string = string.trim().toLowerCase(Locale.ROOT);

		boolean enchanted = false;
		for (String enchant : ENCHANTMENT_NAMES) {
			if (matches(string, enchant)) {
				enchanted = true;
				string = removeFromFront(string, enchant);
				break;
			}
		}

		int color = -1;

		if (enchanted) {
			for (DyeColor dyeColor : DyeColor.values()) {
				String key = dyeColor.getSerializedName().replace("_", " ");
				if (matches(string, key)) {
					color = dyeColor.getId();
					string = removeFromFront(string, key);
					break;
				} else if (key.contains("gray")) {
					key = key.replace("gray", "grey");
					if (matches(string, key)) {
						color = dyeColor.getId();
						string = removeFromFront(string, key);
						break;
					}
				}
			}
			if (color == -1) {
				for (String rainbow : RAINBOW_NAMES) {
					if (matches(string, rainbow)) {
						color = 16;
						string = removeFromFront(string, rainbow);
						break;
					}
				}
			}
		}

		return new TinyPotatoInfo(color, enchanted, string);
	}
}
