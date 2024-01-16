package vazkii.quark.base.recipe.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.quark.base.handler.BrewingHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author WireSegal
 * Created at 5:10 PM on 9/23/19.
 */
public class PotionIngredient extends Ingredient {
	private final Item item;
	private final Potion potion;

	public PotionIngredient(Item item, Potion potion) {
		super(Stream.of(new Ingredient.ItemValue(BrewingHandler.of(item, potion))));
		this.item = item;
		this.potion = potion;
	}

	@Override
	public boolean test(@Nullable ItemStack input) {
		if (input == null)
			return false;
		//Can't use areItemStacksEqualUsingNBTShareTag because it compares stack size as well
		return item == input.getItem() && PotionUtils.getPotion(input) == potion;
	}

	@Override
	public boolean isSimple() {
		return false;
	}

	@Nonnull
	@Override
	public IIngredientSerializer<? extends Ingredient> getSerializer() {
		return PotionIngredient.Serializer.INSTANCE;
	}

	@Nonnull
	@Override
	public JsonElement toJson() {
		JsonObject json = new JsonObject();
		json.addProperty("type", Objects.toString(CraftingHelper.getID(PotionIngredient.Serializer.INSTANCE)));
		json.addProperty("item", Objects.toString(Registry.ITEM.getKey(item)));
		json.addProperty("potion", Objects.toString(Registry.POTION.getKey(potion)));
		return json;
	}

	public static class Serializer implements IIngredientSerializer<PotionIngredient> {
		public static final PotionIngredient.Serializer INSTANCE = new PotionIngredient.Serializer();

		@Nonnull
		@Override
		public PotionIngredient parse(@Nonnull FriendlyByteBuf buffer) {
			Item item = ForgeRegistries.ITEMS.getValue(buffer.readResourceLocation());
			Potion potion = ForgeRegistries.POTIONS.getValue(buffer.readResourceLocation());
			return new PotionIngredient(item, potion);
		}

		@Nonnull
		@Override
		public PotionIngredient parse(@Nonnull JsonObject json) {
			Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(json.getAsJsonPrimitive("item").getAsString()));
			Potion potion = ForgeRegistries.POTIONS.getValue(new ResourceLocation(json.getAsJsonPrimitive("potion").getAsString()));
			return new PotionIngredient(item, potion);
		}

		@Override
		public void write(@Nonnull FriendlyByteBuf buffer, @Nonnull PotionIngredient ingredient) {
			buffer.writeUtf(Objects.toString(ForgeRegistries.ITEMS.getKey(ingredient.item)));
			buffer.writeUtf(Objects.toString(ForgeRegistries.POTIONS.getKey(ingredient.potion)));
		}
	}
}
