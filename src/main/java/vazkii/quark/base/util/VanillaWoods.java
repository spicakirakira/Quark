package vazkii.quark.base.util;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class VanillaWoods {

	public static record Wood(String name, Block log, Block wood, Block planks, Block leaf, Block fence, boolean nether) { }

	public static Wood OAK = new Wood("oak", Blocks.OAK_LOG, Blocks.OAK_WOOD, Blocks.OAK_PLANKS, Blocks.OAK_LEAVES, Blocks.OAK_FENCE, false);
	public static Wood SPRUCE = new Wood("spruce", Blocks.SPRUCE_LOG, Blocks.SPRUCE_WOOD, Blocks.SPRUCE_PLANKS, Blocks.SPRUCE_LEAVES, Blocks.SPRUCE_FENCE, false);
	public static Wood BIRCH = new Wood("birch", Blocks.BIRCH_LOG, Blocks.BIRCH_WOOD, Blocks.BIRCH_PLANKS, Blocks.BIRCH_LEAVES, Blocks.BIRCH_FENCE, false);
	public static Wood JUNGLE = new Wood("jungle", Blocks.JUNGLE_LOG, Blocks.JUNGLE_WOOD, Blocks.JUNGLE_PLANKS, Blocks.JUNGLE_LEAVES, Blocks.JUNGLE_FENCE, false);
	public static Wood ACACIA = new Wood("acacia", Blocks.ACACIA_LOG, Blocks.ACACIA_WOOD, Blocks.ACACIA_PLANKS, Blocks.ACACIA_LEAVES, Blocks.ACACIA_FENCE, false);
	public static Wood DARK_OAK = new Wood("dark_oak", Blocks.DARK_OAK_LOG, Blocks.DARK_OAK_WOOD, Blocks.DARK_OAK_PLANKS, Blocks.DARK_OAK_LEAVES, Blocks.DARK_OAK_FENCE, false);
	public static Wood MANGROVE = new Wood("mangrove", Blocks.MANGROVE_LOG, Blocks.MANGROVE_WOOD, Blocks.MANGROVE_PLANKS, Blocks.MANGROVE_LEAVES, Blocks.MANGROVE_FENCE, false);

	public static Wood CRIMSON = new Wood("crimson", Blocks.CRIMSON_STEM, Blocks.CRIMSON_HYPHAE, Blocks.CRIMSON_PLANKS, null, Blocks.CRIMSON_FENCE, true);
	public static Wood WARPED = new Wood("warped", Blocks.WARPED_STEM, Blocks.WARPED_HYPHAE, Blocks.WARPED_PLANKS, null, Blocks.WARPED_FENCE, true);

	public static final Wood[] OVERWORLD_NON_OAK = new Wood[] {
			SPRUCE, BIRCH, JUNGLE, ACACIA, DARK_OAK, MANGROVE
	};

	public static final Wood[] OVERWORLD = new Wood[] {
			OAK, SPRUCE, BIRCH, JUNGLE, ACACIA, DARK_OAK, MANGROVE
	};

	public static final Wood[] NETHER = new Wood[] {
			CRIMSON, WARPED
	};

	public static final Wood[] ALL = new Wood[] {
			OAK, SPRUCE, BIRCH, JUNGLE, ACACIA, DARK_OAK, CRIMSON, WARPED, MANGROVE
	};

	public static final Wood[] NON_OAK = new Wood[] {
			SPRUCE, BIRCH, JUNGLE, ACACIA, DARK_OAK, CRIMSON, WARPED, MANGROVE
	};
}
