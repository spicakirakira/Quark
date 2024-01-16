//package vazkii.quark.content.tools.item;
//
//import net.minecraft.core.BlockPos;
//import net.minecraft.world.InteractionResult;
//import net.minecraft.world.item.CreativeModeTab;
//import net.minecraft.world.item.Item;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.context.UseOnContext;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.block.Block;
//import net.minecraft.world.level.block.state.BlockState;
//import vazkii.arl.util.ItemNBTHelper;
//import vazkii.quark.base.item.QuarkItem;
//import vazkii.quark.base.module.QuarkModule;
//import vazkii.quark.content.experimental.module.SawModule;
//
//public class SawItem extends QuarkItem {
//	
//	private static String TAG_VARIANT = "place_variant";
//	
//	public SawItem(QuarkModule module) {
//		super("saw", module, new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_TOOLS));
//	}
//	
//	@Override
//	public InteractionResult useOn(UseOnContext ctx) {
//		Level level = ctx.getLevel();
//		BlockPos pos = ctx.getClickedPos();
//		BlockState state = level.getBlockState(pos);
//		Block block = state.getBlock();
//		
//		String variant = SawModule.variants.getVariantForBlock(block);
//		if(variant != null) {
//			ItemNBTHelper.setString(ctx.getItemInHand(), TAG_VARIANT, variant);
//			return InteractionResult.SUCCESS;
//		}
//		
//		return InteractionResult.PASS;
//	}
//	
//	public static String getSavedVariant(ItemStack stack) {
//		return ItemNBTHelper.getString(stack, TAG_VARIANT, "");
//	}
//	
//}
