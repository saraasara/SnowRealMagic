package snownee.snow.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import snownee.snow.ModUtil;
import snownee.snow.SnowCommonConfig;
import snownee.snow.block.entity.SnowCoveredBlockEntity;

public class SnowWallBlock extends WallBlock implements WaterLoggableSnowVariant, WatcherSnowVariant {

	public SnowWallBlock(Properties properties) {
		super(properties);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return ShapeCaches.get(ShapeCaches.COLLIDER, state, worldIn, pos, () -> {
			VoxelShape shape = super.getCollisionShape(state, worldIn, pos, context);
			return Shapes.or(shape, getSnowState(state, worldIn, pos).getCollisionShape(worldIn, pos, context));
		});
	}

	@SuppressWarnings("deprecation")
	@Override
	public VoxelShape getVisualShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return ShapeCaches.get(ShapeCaches.VISUAL, state, worldIn, pos, () -> {
			VoxelShape shape = super.getVisualShape(state, worldIn, pos, context);
			return Shapes.or(shape, getSnowState(state, worldIn, pos).getVisualShape(worldIn, pos, context));
		});
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return ShapeCaches.get(ShapeCaches.OUTLINE, state, worldIn, pos, () -> {
			VoxelShape shape = super.getShape(state, worldIn, pos, context);
			return Shapes.or(shape, getSnowState(state, worldIn, pos).getShape(worldIn, pos, context));
		});
	}

	@Override
	public void randomTick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource random) {
		if (SnowCommonConfig.retainOriginalBlocks || ModUtil.shouldMelt(worldIn, pos)) {
			worldIn.setBlockAndUpdate(pos, getRaw(state, worldIn, pos));
		}
	}

	@Override
	public BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState2, LevelAccessor level, BlockPos blockPos, BlockPos blockPos2) {
		SnowCoveredBlockEntity.updateOptions(level, blockPos);
		return super.updateShape(blockState, direction, blockState2, level, blockPos, blockPos2);
	}

}
