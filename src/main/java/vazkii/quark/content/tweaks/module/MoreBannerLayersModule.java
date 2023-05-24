package vazkii.quark.content.tweaks.module;

import java.util.List;
import java.util.function.BiConsumer;

import net.minecraft.data.BuiltinRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import vazkii.quark.base.handler.MiscUtil;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;

@LoadModule(category = ModuleCategory.TWEAKS)
public class MoreBannerLayersModule extends QuarkModule {

	@Config
	@Config.Min(1)
	@Config.Max(16)
	public static int layerLimit = 16;
	
	private static boolean staticEnabled;
	
	@Override
	public void configChanged() {
		staticEnabled = enabled;
	}
	
	@Override
	public void addAdditionalHints(BiConsumer<Item, Component> consumer) {
		Component comp = Component.translatable("quark.jei.hint.banner_layer_buff", layerLimit);
		
		List<Item> banners = MiscUtil.getTagValues(BuiltinRegistries.ACCESS, ItemTags.BANNERS);
		for(Item item : banners)
			consumer.accept(item, comp);
	}
	
	public static int getLimit(int curr) {
		return staticEnabled ? layerLimit : curr;
	}
	
}
