package vazkii.quark.base.recipe.ingredient;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntLists;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import vazkii.quark.base.module.config.ConfigFlagManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.stream.Stream;

/**
 * @author WireSegal
 * Created at 3:44 PM on 10/20/19.
 */
public class FlagIngredient extends Ingredient {
	private final Ingredient parent;
	private final String flag;

	public FlagIngredient(Ingredient parent, String flag) {
		super(Stream.of());
		this.parent = parent;
		this.flag = flag;
	}

	private boolean isEnabled() {
		if (Serializer.INSTANCE == null)
			return false;
		return Serializer.INSTANCE.flagManager.getFlag(flag);
	}

	@Override
	@Nonnull
	public ItemStack[] getItems() {
		if (!isEnabled())
			return new ItemStack[0];
		return parent.getItems();
	}

	@Override
	@Nonnull
	public IntList getStackingIds() {
		if (!isEnabled())
			return IntLists.EMPTY_LIST;
		return parent.getStackingIds();
	}

	@Override
	public boolean test(@Nullable ItemStack target) {
		if (target == null || !isEnabled())
			return false;

		return parent.test(target);
	}

	@Override
	protected void invalidate() {
		// The invalidate method will collect our parent as well
	}

	@Override
	public boolean isSimple() {
		return parent.isSimple();
	}

	@Nonnull
	@Override
	public IIngredientSerializer<? extends Ingredient> getSerializer() {
		return Serializer.INSTANCE;
	}

	public record Serializer(ConfigFlagManager flagManager) implements IIngredientSerializer<FlagIngredient> {

		public static Serializer INSTANCE;

		public Serializer(ConfigFlagManager flagManager) {
			this.flagManager = flagManager;
			INSTANCE = this;
		}

		@Nonnull
		@Override
		public FlagIngredient parse(@Nonnull FriendlyByteBuf buffer) {
			return new FlagIngredient(Ingredient.fromNetwork(buffer), buffer.readUtf());
		}

		@Nonnull
		@Override
		public FlagIngredient parse(@Nonnull JsonObject json) {
			Ingredient value = Ingredient.fromJson(json.get("value"));
			String flag = json.getAsJsonPrimitive("flag").getAsString();
			return new FlagIngredient(value, flag);
		}

		@Override
		public void write(@Nonnull FriendlyByteBuf buffer, @Nonnull FlagIngredient ingredient) {
			ingredient.parent.toNetwork(buffer);
			buffer.writeUtf(ingredient.flag);
		}

	}
}
