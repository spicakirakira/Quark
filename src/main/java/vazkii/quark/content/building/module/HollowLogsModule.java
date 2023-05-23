package vazkii.quark.content.building.module;

import java.util.List;

import org.apache.commons.compress.utils.Lists;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.handler.advancement.QuarkAdvancementHandler;
import vazkii.quark.base.handler.advancement.QuarkGenericTrigger;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.hint.Hint;
import vazkii.quark.base.util.VanillaWoods;
import vazkii.quark.base.util.VanillaWoods.Wood;
import vazkii.quark.content.building.block.HollowLogBlock;
import vazkii.quark.content.building.block.HollowPillarBlock;

@LoadModule(category = ModuleCategory.BUILDING, hasSubscriptions = true)
public class HollowLogsModule extends QuarkModule {

	private static final String TAG_TRYING_TO_CRAWL = "quark:trying_crawl";

	public static QuarkGenericTrigger crawlTrigger;
	
	@Config(flag = "hollow_log_auto_crawl")
	public static boolean enableAutoCrawl = true;
	
	@Hint(key = "hollow_logs", value = "hollow_log_auto_crawl") 
	List<Block> hollowLogs = Lists.newArrayList();

	@Override
	public void register() {
		for(Wood wood : VanillaWoods.ALL)
			hollowLogs.add(new HollowLogBlock(wood.log(), this, !wood.nether()));
		
		crawlTrigger = QuarkAdvancementHandler.registerGenericTrigger("hollow_log_crawl");
	}

	@SubscribeEvent
	public void playerTick(PlayerTickEvent event) {
		if(enableAutoCrawl && event.phase == Phase.START) {
			Player player = event.player;
			BlockPos playerPos = player.blockPosition();
			boolean isTrying = player.isCrouching() && !player.isSwimming() &&
					player.level.getBlockState(playerPos).getCollisionShape(player.level, playerPos).isEmpty();
			boolean wasTrying = player.getPersistentData().getBoolean(TAG_TRYING_TO_CRAWL);
			
			if(isTrying && !wasTrying) {
				Direction dir = player.getDirection();
				BlockPos pos = playerPos.relative(dir);
				
				if(!tryClimb(player, dir, pos))
					tryClimb(player, dir, pos.above());
			}
			
			if(isTrying != wasTrying)
				player.getPersistentData().putBoolean(TAG_TRYING_TO_CRAWL, isTrying);
		}
	}
	
	private boolean tryClimb(Player player, Direction dir, BlockPos pos) {
		BlockState state = player.level.getBlockState(pos);
		Block block = state.getBlock();
		
		if(block instanceof HollowPillarBlock) {
			Axis axis = state.getValue(HollowPillarBlock.AXIS);
			if(axis != Axis.Y && axis == dir.getAxis()) {
				player.setPose(Pose.SWIMMING);
				player.setSwimming(true);
				
				double x = pos.getX() + 0.5 - (dir.getStepX() * 0.4);
				double y = pos.getY() + 0.13;
				double z = pos.getZ() + 0.5 - (dir.getStepZ() * 0.4);
				
				player.setPos(x, y, z);
				
				if(player instanceof ServerPlayer sp)
					crawlTrigger.trigger(sp);
				
				return true;
			}
		}
		
		return false;
	}
}
