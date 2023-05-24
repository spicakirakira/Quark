package vazkii.quark.base.module.hint;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.ConfigFlagManager;

public class HintManager {
	
	public static void loadHints(List<Field> fields, ConfigFlagManager flagManager, QuarkModule module) {
		for(Field f : fields) {
			f.setAccessible(true);
			Hint hint = f.getDeclaredAnnotation(Hint.class);
			
			if(hint != null) {
				HintObject hintObj = new HintObject(flagManager, module, hint, () -> {
					try {
						return Optional.ofNullable(f.get(module));
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				});
				
				module.hints.add(hintObj);
			}
		}
	}
	
	public static void hintItem(BiConsumer<Item, Component> consumer, ItemLike itemLike) {
		Item item = itemLike.asItem();
		ResourceLocation res = RegistryHelper.getRegistryName(item, Registry.ITEM);
		String ns = res.getNamespace();
		String path = res.getPath();
		
		if(ns.equals(Quark.MOD_ID))
			ns = "";
		else ns += ".";
		
		hintItem(consumer, item, ns + path);
	}
	
	public static void hintItem(BiConsumer<Item, Component> consumer, ItemLike itemLike, String key) {
		Item item = itemLike.asItem();
		String hint = String.format("quark.jei.hint." + key); 
		consumer.accept(item, Component.translatable(hint));
	}
	
}
