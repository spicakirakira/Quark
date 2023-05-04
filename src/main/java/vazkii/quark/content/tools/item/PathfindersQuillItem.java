package vazkii.quark.content.tools.item;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.arl.interf.IItemColorProvider;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.quark.base.item.QuarkItem;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.content.tools.module.PathfinderMapsModule;
import vazkii.quark.content.tools.module.PathfinderMapsModule.TradeInfo;

public class PathfindersQuillItem extends QuarkItem implements IItemColorProvider {
	
	public static final String TAG_BIOME = "targetBiome";
	public static final String TAG_COLOR = "targetBiomeColor";

	public PathfindersQuillItem(QuarkModule module) {
		super("pathfinders_quill", module, new Item.Properties().tab(CreativeModeTab.TAB_TOOLS).stacksTo(1));
	}

	public static ResourceLocation getTargetBiome(ItemStack stack) {
		String str = ItemNBTHelper.getString(stack, TAG_BIOME, "");
		if(str.isEmpty())
			return null;
		
		return new ResourceLocation(str);
	}
	
	public static int getOverlayColor(ItemStack stack) {
		return ItemNBTHelper.getInt(stack, TAG_COLOR, 0xFFFFFF);
	}
	
	public static ItemStack forBiome(String biome, int color) {
		ItemStack stack = new ItemStack(PathfinderMapsModule.pathfinders_quill);
		ItemNBTHelper.setString(stack, TAG_BIOME, biome);
		ItemNBTHelper.setInt(stack, TAG_COLOR, color);
		return stack;
	}
	
	@Override
	public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
		if((isEnabled() && allowedIn(group)) || group == CreativeModeTab.TAB_SEARCH) {
			for(TradeInfo trade : PathfinderMapsModule.tradeList)
				items.add(forBiome(trade.biome.toString(), trade.color));
		}
	}
	
	@Override
	public void appendHoverText(ItemStack stack, Level level, List<Component> comps, TooltipFlag flags) {
		ResourceLocation biome = getTargetBiome(stack); 
		if(biome != null)
			comps.add(Component.translatable("biome." + biome.getNamespace() + "." + biome.getPath()).withStyle(ChatFormatting.GRAY));
		else comps.add(Component.translatable("item.quark.pathfinders_quill_unset").withStyle(ChatFormatting.GRAY));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public ItemColor getItemColor() {
		return (stack, id) -> id == 0 ? 0xFFFFFF : getOverlayColor(stack);
	}
	
}
