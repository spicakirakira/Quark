package vazkii.quark.content.experimental.item;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import vazkii.quark.base.item.QuarkItem;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.content.experimental.module.VariantSelectorModule;
import vazkii.quark.content.tweaks.module.LockRotationModule;

public class HammerItem extends QuarkItem {

	public HammerItem(QuarkModule module) {
		super("hammer", module, new Item.Properties()
				.stacksTo(1)
				.tab(CreativeModeTab.TAB_TOOLS));
	}
	
	@Nonnull
	@Override
	public InteractionResult useOn(UseOnContext context) {
		Player player = context.getPlayer();
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		BlockState state = level.getBlockState(pos);
		Block block = state.getBlock();
		
		String variant = VariantSelectorModule.getSavedVariant(player);
		Block variantBlock = VariantSelectorModule.getVariantOrOriginal(block, variant);
		if(variantBlock != null) {
			BlockPlaceContext bpc = new BlockPlaceContext(context);
			BlockState place = variantBlock.getStateForPlacement(bpc);
			place = LockRotationModule.fixBlockRotation(place, bpc);
			
			if(!place.equals(state)) {
				level.removeBlock(pos, false);
				level.setBlock(pos, place, 1 | 2);
				player.swing(context.getHand());
				
				level.playSound(null, pos, variantBlock.getSoundType(place).getPlaceSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
			}
			
			return InteractionResult.SUCCESS;
		}
		
		return InteractionResult.PASS;
	}

}
