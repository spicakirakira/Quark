package vazkii.quark.base.recipe;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraft.world.level.Level;
import vazkii.quark.base.handler.DyeHandler;

// copy of ArmorDyeRecipe but tweaked for our system 
public class DyeRecipe extends CustomRecipe {
	
	public static final SimpleRecipeSerializer<?> SERIALIZER = new SimpleRecipeSerializer<>(DyeRecipe::new);
	
	public DyeRecipe(ResourceLocation p_43757_) {
		super(p_43757_);
	}

	@Override
	public boolean matches(CraftingContainer p_43769_, Level p_43770_) {
		ItemStack itemstack = ItemStack.EMPTY;
		List<ItemStack> list = Lists.newArrayList();

		for(int i = 0; i < p_43769_.getContainerSize(); ++i) {
			ItemStack itemstack1 = p_43769_.getItem(i);
			if (!itemstack1.isEmpty()) {
				if (DyeHandler.isDyeable(itemstack1)) { // <- changed
					if (!itemstack.isEmpty()) {
						return false;
					}

					itemstack = itemstack1;
				} else {
					if (!(itemstack1.getItem() instanceof DyeItem)) {
						return false;
					}

					list.add(itemstack1);
				}
			}
		}

		return !itemstack.isEmpty() && !list.isEmpty();
	}

	@Override
	public ItemStack assemble(CraftingContainer p_43767_) {
		List<DyeItem> list = Lists.newArrayList();
		ItemStack itemstack = ItemStack.EMPTY;

		for(int i = 0; i < p_43767_.getContainerSize(); ++i) {
			ItemStack itemstack1 = p_43767_.getItem(i);
			if (!itemstack1.isEmpty()) {
				Item item = itemstack1.getItem();
				if (DyeHandler.isDyeable(itemstack1)) {
					if (!itemstack.isEmpty()) {
						return ItemStack.EMPTY;
					}

					itemstack = itemstack1.copy();
				} else {
					if (!(item instanceof DyeItem)) {
						return ItemStack.EMPTY;
					}

					list.add((DyeItem)item);
				}
			}
		}

		return !itemstack.isEmpty() && !list.isEmpty() ? DyeHandler.dyeItem(itemstack, list) : ItemStack.EMPTY;
	}

	@Override
	public boolean canCraftInDimensions(int p_43759_, int p_43760_) {
		return p_43759_ * p_43760_ >= 2;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}
}
