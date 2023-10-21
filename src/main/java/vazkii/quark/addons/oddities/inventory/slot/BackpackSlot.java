package vazkii.quark.addons.oddities.inventory.slot;

import org.jetbrains.annotations.NotNull;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import vazkii.quark.addons.oddities.module.BackpackModule;

public class BackpackSlot extends CachedItemHandlerSlot {

	public BackpackSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
	}
	
	@Override
	public boolean mayPlace(@NotNull ItemStack stack) {
		return super.mayPlace(stack) && !stack.is(BackpackModule.backpackBlockedTag);
	}

}
