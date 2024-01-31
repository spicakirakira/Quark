package org.violetmoon.quark.content.building.block;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.quark.base.Quark;
import org.violetmoon.zeta.block.ZetaBlock;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.registry.RenderLayerRegistry;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WoodPostBlock extends ZetaBlock implements SimpleWaterloggedBlock {

	private static float START = 0F;
	private static float END = 16F;
	private static float LEFT_EDGE = 6F;
	private static float RIGHT_EDGE = 10F;
	
	private static final VoxelShape CENTER_SHAPE = Block.box(LEFT_EDGE, LEFT_EDGE, LEFT_EDGE, RIGHT_EDGE, RIGHT_EDGE, RIGHT_EDGE);

	private static final VoxelShape DOWN_SHAPE = Block.box(LEFT_EDGE, START, LEFT_EDGE, RIGHT_EDGE, RIGHT_EDGE, RIGHT_EDGE);
	private static final VoxelShape UP_SHAPE = Block.box(LEFT_EDGE, LEFT_EDGE, LEFT_EDGE, RIGHT_EDGE, END, RIGHT_EDGE);
	private static final VoxelShape NORTH_SHAPE = Block.box(LEFT_EDGE, LEFT_EDGE, START, RIGHT_EDGE, RIGHT_EDGE, RIGHT_EDGE);
	private static final VoxelShape SOUTH_SHAPE = Block.box(LEFT_EDGE, LEFT_EDGE, LEFT_EDGE, RIGHT_EDGE, RIGHT_EDGE, END);
	private static final VoxelShape WEST_SHAPE = Block.box(START, LEFT_EDGE, LEFT_EDGE, RIGHT_EDGE, RIGHT_EDGE, RIGHT_EDGE);
	private static final VoxelShape EAST_SHAPE = Block.box(LEFT_EDGE, LEFT_EDGE, LEFT_EDGE, END, RIGHT_EDGE, RIGHT_EDGE);

	private static final VoxelShape[] SIDE_BOXES = new VoxelShape[] {
			DOWN_SHAPE, UP_SHAPE, NORTH_SHAPE, SOUTH_SHAPE, WEST_SHAPE, EAST_SHAPE
	};

	private static final VoxelShape[] SHAPE_CACHE = new VoxelShape[64 * 3];
	
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	public static final EnumProperty<Axis> AXIS = BlockStateProperties.AXIS;

	@SuppressWarnings("unchecked")
	public static final EnumProperty<PostSideType>[] SIDES = new EnumProperty[] {
			EnumProperty.create("connect_down", PostSideType.class),
			EnumProperty.create("connect_up", PostSideType.class),
			EnumProperty.create("connect_north", PostSideType.class),
			EnumProperty.create("connect_south", PostSideType.class),
			EnumProperty.create("connect_west", PostSideType.class),
			EnumProperty.create("connect_east", PostSideType.class)
	};

	public WoodPostBlock(@Nullable ZetaModule module, Block parent, String prefix, SoundType sound) {
		super(Quark.ZETA.registryUtil.inherit(parent, s -> prefix + s.replace("_fence", "_post")),
				module,
				Properties.copy(parent).sound(sound));

		BlockState state = stateDefinition.any().setValue(WATERLOGGED, false).setValue(AXIS, Axis.Y);
		for(EnumProperty<PostSideType> prop : SIDES)
			state = state.setValue(prop, PostSideType.NONE);
		registerDefaultState(state);

		if(module == null) //auto registration below this line
			return;

		module.zeta.renderLayerRegistry.put(this, RenderLayerRegistry.Layer.CUTOUT);
		setCreativeTab(CreativeModeTabs.BUILDING_BLOCKS, parent, true);
	}

	@NotNull
	@Override
	public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter worldIn, @NotNull BlockPos pos, @NotNull CollisionContext context) {
		int index = 0;
		for(Direction dir : Direction.values()) {
			if(state.getValue(SIDES[dir.ordinal()]).isSolid())
				index += (1 << dir.ordinal());
		}
		index += (64 * state.getValue(AXIS).ordinal());

		VoxelShape cached = SHAPE_CACHE[index];
		if(cached == null) {
			VoxelShape currShape = CENTER_SHAPE;

			for(Direction dir : Direction.values()) {
				boolean connected = isConnected(state, dir);
				if(connected)
					currShape = Shapes.or(currShape, SIDE_BOXES[dir.ordinal()]);
			}

			SHAPE_CACHE[index] = currShape;
			cached = currShape;
		}

		return cached;
	}
	
	private boolean isConnected(BlockState state, Direction dir) {
		if(state.getValue(AXIS) == dir.getAxis())
			return true;
		
		return state.getValue(SIDES[dir.ordinal()]).isSolid();
	}
	
	@Override
	public boolean propagatesSkylightDown(BlockState state, @NotNull BlockGetter reader, @NotNull BlockPos pos) {
		return !state.getValue(WATERLOGGED);
	}

	@NotNull
	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return getState(context.getLevel(), context.getClickedPos(), context.getClickedFace().getAxis());
	}

	@NotNull
	@Override
	public BlockState updateShape(BlockState state, @NotNull Direction facing, @NotNull BlockState facingState, @NotNull LevelAccessor level, @NotNull BlockPos pos, @NotNull BlockPos facingPos) {
		if(state.getValue(WATERLOGGED)) {
			level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}

		return super.updateShape(state, facing, facingState, level, pos, facingPos);
	}

	@Override
	public void neighborChanged(@NotNull BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, @NotNull Block blockIn, @NotNull BlockPos fromPos, boolean isMoving) {
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);

		BlockState newState = getState(worldIn, pos, state.getValue(AXIS));
		if(!newState.equals(state))
			worldIn.setBlockAndUpdate(pos, newState);
	}

	private BlockState getState(Level world, BlockPos pos, Axis axis) {
		BlockState state = defaultBlockState().setValue(WATERLOGGED, world.getFluidState(pos).getType() == Fluids.WATER).setValue(AXIS, axis);

		for(Direction d : Direction.values()) {
			if(d.getAxis() == axis)
				continue;
			
			EnumProperty<PostSideType> prop = SIDES[d.ordinal()];
			BlockState sideState = world.getBlockState(pos.relative(d));
			
			if((sideState.getBlock() instanceof ChainBlock && sideState.getValue(BlockStateProperties.AXIS) == d.getAxis())
					|| (d == Direction.DOWN && sideState.getBlock() instanceof LanternBlock && sideState.getValue(LanternBlock.HANGING))
					|| (d == Direction.DOWN && sideState.getBlock() instanceof CeilingHangingSignBlock)) {
				
				state = state.setValue(prop, PostSideType.CHAIN);
				continue;
			}
			
			if(sideState.getBlock() instanceof WoodPostBlock && sideState.getValue(AXIS) == d.getAxis()) {
				state = state.setValue(prop, PostSideType.OTHER_POST);
				continue;
			}
		}

		return state;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(WATERLOGGED, AXIS);
		for(EnumProperty<PostSideType> prop : SIDES)
			builder.add(prop);
	}
	
	public enum PostSideType implements StringRepresentable {
		NONE("none"), 
		CHAIN("chain"), 
		OTHER_POST("other_post");
		
		private String name;
		
		private PostSideType(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
		
		@Override
		public String getSerializedName() {
			return name;
		}
		
		public boolean isSolid() {
			return this != NONE;
		}
		
	}


}
