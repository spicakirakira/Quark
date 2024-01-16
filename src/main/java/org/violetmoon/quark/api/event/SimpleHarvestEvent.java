package org.violetmoon.quark.api.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * Used primarily for double crops which need extra checks before they are considered ready.
 * Ony fires if said block is not blacklisted
 * Can also be used for blocks that hae other max age requirements as it fires for all crop blocks
 */
@Cancelable
public class SimpleHarvestEvent extends Event {

	public final BlockState blockState;
	public final BlockPos pos;
	public final InteractionHand hand;
	public final Player player;
	public final Source type;
	private BlockPos newTarget;
	private ActionType action;

	public SimpleHarvestEvent(BlockState blockState, BlockPos pos, InteractionHand hand,
			Player player, boolean isHoe, ActionType actionType) {
		this.blockState = blockState;
		this.pos = pos;
		this.hand = hand;
		this.player = player;
		this.newTarget = pos;
		this.type = isHoe ? Source.HOE : Source.RIGHT_CLICK;
		this.action = actionType;
	}

	/**
	 * Used for double crops and the like. Pass a new position which should be broken instead
	 *
	 * @param pos new target position
	 */
	public void setTargetPos(BlockPos pos) {
		this.newTarget = pos;
	}

	public Source getInteractionSource() {
		return type;
	}

	@Override
	public void setCanceled(boolean cancel) {
		if(cancel)
			action = ActionType.NONE;
		super.setCanceled(cancel);
	}

	public enum ActionType {
		NONE, CLICK, HARVEST;
	}

	public ActionType getAction() {
		return action;
	}

	public void setAction(ActionType action) {
		this.action = action;
	}

	public BlockPos getTargetPos() {
		return newTarget;
	}

	public enum Source {
		RIGHT_CLICK, HOE
	}
}
