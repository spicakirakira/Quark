package vazkii.quark.content.automation.block;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.AABB;
import vazkii.quark.base.block.QuarkPressurePlateBlock;
import vazkii.quark.base.module.QuarkModule;

/**
 * @author WireSegal
 * Created at 9:47 PM on 10/8/19.
 */
public class ObsidianPressurePlateBlock extends QuarkPressurePlateBlock {
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

	public ObsidianPressurePlateBlock(String regname, QuarkModule module, CreativeModeTab creativeTab, Properties properties) {
		super(null /*Sensitivity is unused*/, regname, module, creativeTab, properties);
		this.registerDefaultState(defaultBlockState().setValue(POWERED, false));
	}

	@Override
	protected void playOnSound(@Nonnull LevelAccessor worldIn, @Nonnull BlockPos pos) {
		worldIn.playSound(null, pos, SoundEvents.STONE_PRESSURE_PLATE_CLICK_ON, SoundSource.BLOCKS, 0.3F, 0.5F);
	}

	@Override
	protected void playOffSound(@Nonnull LevelAccessor worldIn, @Nonnull BlockPos pos) {
		worldIn.playSound(null, pos, SoundEvents.STONE_PRESSURE_PLATE_CLICK_OFF, SoundSource.BLOCKS, 0.3F, 0.4F);
	}

	@Override
	protected int getSignalStrength(@Nonnull Level worldIn, @Nonnull BlockPos pos) {
		AABB bounds = TOUCH_AABB.move(pos);
		List<? extends Entity> entities = worldIn.getEntitiesOfClass(Player.class, bounds);

		if (!entities.isEmpty()) {
			for(Entity entity : entities) {
				if (!entity.isIgnoringBlockTriggers()) {
					return 15;
				}
			}
		}

		return 0;
	}
}
