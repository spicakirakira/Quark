package vazkii.quark.addons.oddities.inventory.slot;

import com.google.common.cache.Cache;
import net.minecraftforge.items.IItemHandler;

@Deprecated(forRemoval = true)
public class SlotCachingItemHandler extends CachedItemHandlerSlot {
    public SlotCachingItemHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }
}
