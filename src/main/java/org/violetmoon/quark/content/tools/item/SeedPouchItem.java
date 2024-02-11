package org.violetmoon.quark.content.tools.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;

import org.jetbrains.annotations.NotNull;

import org.jetbrains.annotations.Nullable;
import org.violetmoon.quark.api.ITrowelable;
import org.violetmoon.quark.api.IUsageTickerOverride;
import org.violetmoon.quark.base.Quark;
import org.violetmoon.quark.content.management.module.ItemSharingModule;
import org.violetmoon.quark.content.tools.module.SeedPouchModule;
import org.violetmoon.zeta.item.ZetaItem;
import org.violetmoon.zeta.module.IDisableable;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.registry.CreativeTabManager;
import org.violetmoon.zeta.util.ItemNBTHelper;
import org.violetmoon.zeta.util.RegistryUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class SeedPouchItem extends ZetaItem implements IUsageTickerOverride, ITrowelable, CreativeTabManager.AppendsUniquely {


	private static final int BAR_COLOR = Mth.color(0.4F, 0.4F, 1.0F);

	public SeedPouchItem(ZetaModule module) {
		super("seed_pouch", module,
				new Item.Properties()
						.stacksTo(1));
		CreativeTabManager.addToCreativeTabNextTo(CreativeModeTabs.TOOLS_AND_UTILITIES, this, Items.LEAD, false);
	}

	@Override
	public boolean overrideOtherStackedOnMe(@NotNull ItemStack pouch, @NotNull ItemStack incoming, @NotNull Slot slot, @NotNull ClickAction action, @NotNull Player player, @NotNull SlotAccess carriedSlotAccessor) {
		if(pouch.getCount() != 1 || action != ClickAction.SECONDARY || !slot.allowModification(player))
			return false;

		//quick note: incoming == carriedSlotAccessor.get(). in all cases i've observed they are the same object -quat

		if(incoming.isEmpty())
			//right click pouch with an empty cursor -> take items out of the pouch and put them on the cursor
			return dropOntoEmptyCursor(player, pouch, carriedSlotAccessor);
		else
			//right click pouch while holding something on cursor -> take items from the cursor and try to put them into the pouch
			return absorbFromCursor(player, pouch, carriedSlotAccessor);
	}

	@Override
	public boolean overrideStackedOnOther(@NotNull ItemStack pouch, @NotNull Slot slot, @NotNull ClickAction action, @NotNull Player player) {
		if(pouch.getCount() != 1 || action != ClickAction.SECONDARY || !slot.allowModification(player))
			return false;

		ItemStack droppedOnto = slot.getItem();
		if(droppedOnto.isEmpty())
			//right click an empty slot with a pouch -> take items from the pouch and drop them into the empty slot
			return dropIntoEmptySlot(player, pouch, slot);
		else
			//right click on something with a pouch -> take items from the slot and try to put them in the pouch
			return absorbFromSlot(player, pouch, slot);
	}

	private boolean absorbFromCursor(Player player, ItemStack pouch, SlotAccess cursorAccess) {
		return mutateContents(pouch, contents -> {
			ItemStack onCursor = cursorAccess.get();

			if(!contents.absorb(onCursor))
				return false;

			cursorAccess.set(onCursor); //IDK if you need to call "set" on the SlotAccess here but it doesn't hurt
			playInsertSound(player);
			return true;
		});
	}

	private boolean absorbFromSlot(Player player, ItemStack pouch, Slot pickupFrom) {
		return mutateContents(pouch, contents -> {
			if(!contents.absorb(pickupFrom.getItem()))
				return false;

			pickupFrom.setChanged();
			playInsertSound(player);
			return true;
		});
	}

	private boolean dropOntoEmptyCursor(Player player, ItemStack pouch, SlotAccess cursorAccess) {
		return mutateContents(pouch, contents -> {
			if(contents.isEmpty())
				return false;

			cursorAccess.set(contents.splitOneStack());
			playRemoveOneSound(player);
			return true;
		});
	}

	private boolean dropIntoEmptySlot(Player player, ItemStack pouch, Slot depositInto) {
		return mutateContents(pouch, contents -> {
			if(contents.isEmpty())
				return false;

			depositInto.set(contents.splitOneStack());
			playRemoveOneSound(player);
			return true;
		});
	}

	@Override
	public boolean isEnchantable(@NotNull ItemStack stack) {
		return false;
	}

	// vanilla copy
	private static void playRemoveOneSound(Entity entity) {
		entity.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
	}

	private static void playInsertSound(Entity entity) {
		entity.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
	}

	@Override
	public boolean isBarVisible(@NotNull ItemStack stack) {
		return true;
	}

	@Override
	public int getBarWidth(@NotNull ItemStack stack) {
		int count = getCount(stack);
		return Math.round(count * 13.0F / SeedPouchModule.maxItems);
	}

	@Override
	public int getBarColor(@NotNull ItemStack stack) {
		return BAR_COLOR;
	}

	public static SeedPouchItem.PouchContents getContents(ItemStack stack) {
		return PouchContents.readFromStack(stack);
	}

	public static int getCount(ItemStack stack) {
		return PouchContents.readCountOnlyFromStack(stack);
	}

	public static <T> T mutateContents(ItemStack pouch, Function<PouchContents, T> func) {
		return PouchContents.mutate(pouch, func);
	}

	@NotNull
	@Override
	public Component getName(@NotNull ItemStack stack) {
		Component base = super.getName(stack);

		PouchContents contents = getContents(stack);
		if(contents.isEmpty())
			return base;

		MutableComponent comp = base.copy();
		comp.append(Component.literal(" ("));
		comp.append(contents.getSeed().getHoverName());
		comp.append(Component.literal(")"));
		return comp;
	}

	@NotNull
	@Override
	public InteractionResult useOn(UseOnContext context) {
		ItemStack pouch = context.getItemInHand();

		return mutateContents(pouch, contents -> {
			if(contents.isEmpty())
				return super.useOn(context);

			ItemStack seed = contents.getSeed().copy();
			int total = contents.count;
			seed.setCount(Math.min(seed.getMaxStackSize(), total));

			Player player = context.getPlayer();
			if(player == null || !player.isShiftKeyDown())
				return placeSeed(contents, context, seed, context.getClickedPos());

			InteractionResult bestRes = InteractionResult.FAIL;
			int range = SeedPouchModule.shiftRange;
			int blocks = range * range;
			int shift = -((int) Math.floor(range / 2f));

			for(int i = 0; i < blocks; i++) {
				int x = shift + i % range;
				int z = shift + i / range;

				InteractionResult res = placeSeed(contents, context, seed, context.getClickedPos().offset(x, 0, z));

				if(contents.isEmpty())
					break;

				if(!bestRes.consumesAction())
					bestRes = res;
			}

			return bestRes;
		});
	}

	private InteractionResult placeSeed(PouchContents mutableContents, UseOnContext context, ItemStack seed, BlockPos pos) {
		@Nullable Player player = context.getPlayer();

		InteractionResult res;
		if(player == null) {
			res = seed.getItem().useOn(new PouchItemUseContext(context, seed, pos));
		} else {
			//Do a little switcheroo, so the player actually holds the seed they're about to place.
			ItemStack restore = player.getItemInHand(context.getHand()); //the seed pouch
			player.setItemInHand(context.getHand(), seed);
			res = seed.getItem().useOn(new PouchItemUseContext(context, seed, pos));
			player.setItemInHand(context.getHand(), restore);
		}

		int itemsToTake = res == InteractionResult.CONSUME ? 1 : 0;

		if(itemsToTake != 0 && (player == null || !player.getAbilities().instabuild))
			mutableContents.shrink(itemsToTake);

		return res;
	}

	@Override
	public List<ItemStack> appendItemsToCreativeTab() {
		if(!isEnabled())
			return List.of();

		List<ItemStack> list = new ArrayList<>();
		list.add(new ItemStack(this));

		if(SeedPouchModule.showAllVariantsInCreative) {
			RegistryAccess access = Quark.proxy.hackilyGetCurrentClientLevelRegistryAccess();
			if(access != null) {
				for(Item seed : RegistryUtil.getTagValues(access, SeedPouchModule.seedPouchHoldableTag)) {
					if(!IDisableable.isEnabled(seed))
						continue;

					PouchContents contents = new PouchContents();
					contents.setSeed(new ItemStack(seed));
					contents.setCount(SeedPouchModule.maxItems);
					list.add(contents.writeToStack(new ItemStack(this)));
				}
			}

		}

		return list;
	}

	@Override
	public ItemStack getUsageTickerItem(ItemStack stack) {
		PouchContents contents = getContents(stack);
		return contents.isEmpty() ? stack : contents.getSeed();
	}

	@Override
	public int getUsageTickerCountForItem(ItemStack stack, Predicate<ItemStack> target) {
		PouchContents contents = getContents(stack);
		return !contents.isEmpty() && target.test(contents.getSeed()) ? contents.getCount() : 0;
	}

	@NotNull
	@Override
	public Optional<TooltipComponent> getTooltipImage(@NotNull ItemStack stack) {
		return getCount(stack) == 0 ? Optional.empty() : Optional.of(new Tooltip(stack));
	}

	public record Tooltip(ItemStack stack) implements TooltipComponent {}

	public static class PouchItemUseContext extends UseOnContext {

		protected PouchItemUseContext(UseOnContext parent, ItemStack stack, BlockPos targetPos) {
			super(parent.getLevel(), parent.getPlayer(), parent.getHand(), stack,
					new BlockHitResult(parent.getClickLocation(), parent.getClickedFace(), targetPos, parent.isInside()));
		}

	}

	public static class PouchContents {

		public static final String TAG_STORED_ITEM = "storedItem";
		public static final String TAG_COUNT = "itemCount";

		private ItemStack seed = ItemStack.EMPTY; //Always has 0x count (empty) or 1x count (nonempty).
		private int count = 0;

		public ItemStack writeToStack(ItemStack target) {
			CompoundTag tag = target.getTag();

			if(isEmpty()) {
				//If we are empty, and the target doesn't have any NBT tag, that's cool. Nothing to do.
				//If the target *does* have an NBT tag, ensure we remove our tags.
				if(tag != null) {
					tag.remove(TAG_STORED_ITEM);
					tag.remove(TAG_COUNT);

					//And, if we just removed *all* the tags on the target, erase its NBT tag entirely.
					if(tag.isEmpty())
						target.setTag(null);
				}
			} else {
				ItemNBTHelper.setCompound(target, TAG_STORED_ITEM, seed.save(new CompoundTag()));
				ItemNBTHelper.setInt(target, TAG_COUNT, count);
			}

			return target;
		}

		public static PouchContents readFromStack(ItemStack target) {
			CompoundTag tag = target.getTag();
			PouchContents contents = new PouchContents();

			if(tag != null && tag.contains(TAG_STORED_ITEM) && tag.contains(TAG_COUNT)) {
				contents.seed = ItemStack.of(tag.getCompound(TAG_STORED_ITEM));
				contents.count = tag.getInt(TAG_COUNT);
			}

			return contents;
		}

		//Invariant: readFromStack(target).getCount() == readCountOnlyFromStack().
		//Reading itemstacks is expensive, often times we don't care about them.
		public static int readCountOnlyFromStack(ItemStack target) {
			return ItemNBTHelper.getInt(target, TAG_COUNT, 0);
		}

		//Ensures you can't read a PouchContents, mutate it, then forget to write it back to the stack.
		public static <T> T mutate(ItemStack pouch, Function<PouchContents, T> action) {
			PouchContents contents = readFromStack(pouch);
			T result = action.apply(contents);
			contents.writeToStack(pouch);

			return result;
		}

		public boolean isEmpty() {
			return seed.isEmpty() || count == 0;
		}

		public ItemStack getSeed() {
			return seed;
		}

		public int getCount() {
			return count;
		}

		public void setSeed(ItemStack seed) {
			this.seed = seed.copy();
			this.seed.setCount(1);
		}

		public void setCount(int newCount) {
			this.count = newCount;
			if(this.count <= 0) {
				this.count = 0;
				this.seed = ItemStack.EMPTY;
			}
		}

		public void grow(int more) {
			setCount(count + more);
		}

		public void shrink(int less) {
			setCount(count - less);
		}

		//Moves items from 'other' into self (mutating both). returns whether it moved any items
		public boolean absorb(ItemStack other) {
			if(!canFit(other))
				return false;

			int toMove = Math.min(SeedPouchModule.maxItems - count, other.getCount());
			if(toMove == 0)
				return false;

			if(this.isEmpty()) {
				setSeed(other);
				setCount(toMove);
			} else
				grow(toMove);

			other.shrink(toMove);
			return true;
		}

		//Mutates self
		public ItemStack split(int request) {
			int howMany = Math.min(count, request);

			ItemStack result = seed.copy();
			result.setCount(howMany);

			shrink(howMany);
			return result;
		}

		//Mutates self
		public ItemStack splitOneStack() {
			return split(seed.getMaxStackSize());
		}

		public boolean canFit(ItemStack other) {
			if(isEmpty())
				return other.is(SeedPouchModule.seedPouchHoldableTag);
			else
				return this.count < SeedPouchModule.maxItems && ItemStack.isSameItemSameTags(seed, other);
		}

	}

}
