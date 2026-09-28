package io.github.jason13official.valcraft_portals.impl.common.block;

import com.mojang.serialization.MapCodec;
import io.github.jason13official.valcraft_portals.impl.common.block.entity.PortalBlockEntity;
import io.github.jason13official.valcraft_portals.impl.common.registry.ModTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PortalBlock extends Block implements EntityBlock {

  public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
  public static final BooleanProperty LIT = BlockStateProperties.LIT;

  // NetherPortalBlock
//  protected static final VoxelShape X_AXIS_AABB = Block.box((double)0.0F, (double)0.0F, (double)6.0F, (double)16.0F, (double)16.0F, (double)10.0F);
//  protected static final VoxelShape Z_AXIS_AABB = Block.box((double)6.0F, (double)0.0F, (double)0.0F, (double)10.0F, (double)16.0F, (double)16.0F);

  protected static final VoxelShape X_AXIS_AABB = Block.box(-16.0F, 0.0F, 6.0F, 32.0F, 48, 10.0F);
  protected static final VoxelShape Z_AXIS_AABB = Block.box(6.0F, 0.0F, -16.0F, 10.0F, 48.0F, 32.0F);

  public static final MapCodec<PortalBlock> CODEC = simpleCodec(PortalBlock::new);

  public PortalBlock(Properties pProperties) {
    super(pProperties);
    this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(LIT, false));
  }

  // region common

  /// @see net.minecraft.world.level.block.BaseEntityBlock
  @SuppressWarnings("unchecked")
  protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> pServerType, BlockEntityType<E> pClientType,
      BlockEntityTicker<? super E> pTicker) {
    return pClientType == pServerType ? (BlockEntityTicker<A>) pTicker : null;
  }

  public MapCodec<PortalBlock> codec() {
    return CODEC;
  }

  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {

    pBuilder.add(FACING, LIT);
  }

  public BlockState getStateForPlacement(BlockPlaceContext pContext) {

    return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
  }

  protected VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
    // switch ((Direction.Axis)pState.getValue(AXIS)) {
    switch ((Direction.Axis)pState.getValue(FACING).getAxis()) {
      case Z:
        return Z_AXIS_AABB;
      case X:
      default:
        return X_AXIS_AABB;
    }
  }

  /// @deprecated
  protected BlockState rotate(BlockState pState, Rotation pRotation) {

    return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
  }

  /// @deprecated
  protected BlockState mirror(BlockState pState, Mirror pMirror) {

    return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
  }

  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {

    if (pLevel.isClientSide()) {
      return createTickerHelper(pBlockEntityType, ModTiles.PORTAL, PortalBlockEntity::tickClient);
    } else {
      return createTickerHelper(pBlockEntityType, ModTiles.PORTAL, PortalBlockEntity::tickServer);
    }
  }

  @Override
  protected boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {

    // TODO check based on axis whether surrounding blocks in the 3x3x1 area (rotated if needed) are air -> only then can the portal survive

    return super.canSurvive(pState, pLevel, pPos);
  }

  // endregion common

  // region client

  protected RenderShape getRenderShape(BlockState pState) {

    return RenderShape.MODEL;
  }

  @Override
  public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
    // super.animateTick(pState, pLevel, pPos, pRandom); // no-op

    if (pState.getValue(LIT)) {
      double d0 = (double) pPos.getX() + (double) 0.5F;
      double d1 = pPos.getY();
      double d2 = (double) pPos.getZ() + (double) 0.5F;
      if (pRandom.nextDouble() < 0.1) {
        pLevel.playLocalSound(d0, d1, d2, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
      }

      Direction direction = pState.getValue(FACING);
      Direction.Axis direction$axis = direction.getAxis();
      double d3 = 0.52;
      double d4 = pRandom.nextDouble() * 0.6 - 0.3;
      double d5 = direction$axis == Axis.X ? (double) direction.getStepX() * 0.52 : d4;
      double d6 = pRandom.nextDouble() * (double) 6.0F / (double) 16.0F;
      double d7 = direction$axis == Axis.Z ? (double) direction.getStepZ() * 0.52 : d4;
      pLevel.addParticle(ParticleTypes.SMOKE, d0 + d5, d1 + d6, d2 + d7, 0.0F, 0.0F, 0.0F);
      pLevel.addParticle(ParticleTypes.FLAME, d0 + d5, d1 + d6, d2 + d7, 0.0F, 0.0F, 0.0F);
    }
  }

  // endregion client

  // region server

  @Override
  public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {

    return new PortalBlockEntity(blockPos, blockState);
  }

  // endregion server
}
