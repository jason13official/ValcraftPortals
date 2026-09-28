package io.github.jason13official.valcraft_portals.impl.common.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.jason13official.valcraft_portals.impl.common.block.entity.PortalBlockEntity;
import io.github.jason13official.valcraft_portals.impl.common.portal.PortalNetwork;
import io.github.jason13official.valcraft_portals.impl.common.portal.PortalTeleporter;
import io.github.jason13official.valcraft_portals.impl.common.registry.ModTiles;
import io.github.jason13official.valcraft_portals.impl.network.packet.OpenPortalTagScreenS2CPacket;
import io.github.jason13official.valcraft_portals.platform.Services;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PortalBlock extends Block implements EntityBlock {

  public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
  public static final BooleanProperty LIT = BlockStateProperties.LIT;
  public static final EnumProperty<PortalPart> PART = EnumProperty.create("part", PortalPart.class);

  public static final MapCodec<PortalBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
      Codec.BOOL.fieldOf("restricts_items").forGetter(PortalBlock::restrictsItems),
      propertiesCodec()
  ).apply(instance, PortalBlock::new));

  private static final Map<Axis, Map<PortalPart, VoxelShape>> SHAPES = new EnumMap<>(Axis.class);

  static {
    for (Axis axis : new Axis[]{Axis.X, Axis.Z}) {
      Map<PortalPart, VoxelShape> shapes = new EnumMap<>(PortalPart.class);
      for (PortalPart part : PortalPart.values()) {
        double along = -part.dx * 16.0;
        double up = -part.dy * 16.0;
        shapes.put(part, axis == Axis.X
            ? Block.box(-16.0 + along, up, 6.0, 32.0 + along, 48.0 + up, 10.0)
            : Block.box(6.0, up, -16.0 + along, 10.0, 48.0 + up, 32.0 + along));
      }
      SHAPES.put(axis, shapes);
    }
  }

  private final boolean restrictsItems;

  public PortalBlock(boolean restrictsItems, Properties pProperties) {
    super(pProperties);
    this.restrictsItems = restrictsItems;
    this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(LIT, false).setValue(PART, PortalPart.BOTTOM));
  }

  public boolean restrictsItems() {
    return restrictsItems;
  }

  public static boolean isMaster(BlockState state) {
    return state.getValue(PART).isMaster();
  }

  public static Direction alongDirection(Direction facing) {
    return Direction.fromAxisAndDirection(facing.getClockWise().getAxis(), Direction.AxisDirection.POSITIVE);
  }

  public static BlockPos partPos(BlockPos master, Direction facing, PortalPart part) {
    return master.relative(alongDirection(facing), part.dx).above(part.dy);
  }

  public static BlockPos masterPos(BlockPos pos, BlockState state) {
    PortalPart part = state.getValue(PART);
    return pos.relative(alongDirection(state.getValue(FACING)), -part.dx).below(part.dy);
  }

  public static AABB travelArea(BlockPos master, Direction facing) {
    double cx = master.getX() + 0.5;
    double cz = master.getZ() + 0.5;
    double y = master.getY();
    return facing.getClockWise().getAxis() == Axis.X
        ? new AABB(cx - 1.3, y + 0.1, cz - 0.1, cx + 1.3, y + 2.7, cz + 0.1)
        : new AABB(cx - 0.1, y + 0.1, cz - 1.3, cx + 0.1, y + 2.7, cz + 1.3);
  }

  // region common

  /// @see net.minecraft.world.level.block.BaseEntityBlock
  @SuppressWarnings("unchecked")
  protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> pServerType, BlockEntityType<E> pClientType,
      BlockEntityTicker<? super E> pTicker) {
    return pClientType == pServerType ? (BlockEntityTicker<A>) pTicker : null;
  }

  @Override
  public MapCodec<PortalBlock> codec() {
    return CODEC;
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {

    pBuilder.add(FACING, LIT, PART);
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext pContext) {

    Direction facing = pContext.getHorizontalDirection().getOpposite();
    BlockPos master = pContext.getClickedPos();
    Level level = pContext.getLevel();

    if (master.getY() + 2 >= level.getMaxBuildHeight()) {
      return null;
    }

    for (PortalPart part : PortalPart.values()) {
      BlockPos pos = partPos(master, facing, part);
      if (!level.getBlockState(pos).canBeReplaced(pContext) || !level.getWorldBorder().isWithinBounds(pos)) {
        return null;
      }
    }

    return this.defaultBlockState().setValue(FACING, facing);
  }

  @Override
  public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {

    if (pLevel.isClientSide()) {
      return;
    }

    Direction facing = pState.getValue(FACING);
    for (PortalPart part : PortalPart.values()) {
      if (!part.isMaster()) {
        pLevel.setBlock(partPos(pPos, facing, part), pState.setValue(PART, part), Block.UPDATE_ALL);
      }
    }
  }

  @Override
  public BlockState playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {

    if (!pLevel.isClientSide() && pPlayer.isCreative() && !isMaster(pState)) {
      BlockPos master = masterPos(pPos, pState);
      BlockState masterState = pLevel.getBlockState(master);

      if (masterState.is(this) && isMaster(masterState) && masterState.getValue(FACING) == pState.getValue(FACING)) {
        pLevel.setBlock(master, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);
        pLevel.levelEvent(null, 2001, master, Block.getId(masterState));
      }
    }

    return super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
  }

  @Override
  protected BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {

    Direction facing = pState.getValue(FACING);
    Direction along = alongDirection(facing);
    PortalPart part = pState.getValue(PART);

    int stepAlong = pDirection == along ? 1 : pDirection == along.getOpposite() ? -1 : 0;
    int stepUp = pDirection.getStepY();

    if (stepAlong != 0 || stepUp != 0) {
      PortalPart expected = PortalPart.at(part.dx + stepAlong, part.dy + stepUp);
      boolean intact = pNeighborState.is(this) && pNeighborState.getValue(FACING) == facing && pNeighborState.getValue(PART) == expected;

      if (expected != null && !intact) {
        return Blocks.AIR.defaultBlockState();
      }
    }

    return super.updateShape(pState, pDirection, pNeighborState, pLevel, pPos, pNeighborPos);
  }

  @Override
  protected VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {

    return SHAPES.get(pState.getValue(FACING).getClockWise().getAxis()).get(pState.getValue(PART));
  }

  @Override
  protected BlockState rotate(BlockState pState, Rotation pRotation) {

    return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
  }

  @Override
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

  // endregion common

  // region client

  @Override
  protected RenderShape getRenderShape(BlockState pState) {

    return RenderShape.INVISIBLE;
  }

  // endregion client

  // region server

  @Override
  protected void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pMovedByPiston) {

    if (!pLevel.isClientSide() && isMaster(pState) && !pOldState.is(this)) {
      pLevel.scheduleTick(pPos, this, 1);
    }
  }

  @Override
  protected void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {

    if (isMaster(pState) && pLevel.getBlockEntity(pPos) instanceof PortalBlockEntity portal) {
      portal.sync(pLevel);
    }
  }

  @Override
  protected void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {

    if (pLevel instanceof ServerLevel level && isMaster(pState) && !pState.is(pNewState.getBlock())) {
      PortalNetwork.get(level.getServer()).remove(level.getServer(), GlobalPos.of(level.dimension(), pPos));
    }

    super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
  }

  @Override
  protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult) {

    if (pPlayer instanceof ServerPlayer player) {
      BlockPos master = masterPos(pPos, pState);
      if (pLevel.getBlockEntity(master) instanceof PortalBlockEntity portal) {
        Services.PLATFORM.sendToPlayer(player, new OpenPortalTagScreenS2CPacket(master, portal.getTag()));
      }
    }

    return InteractionResult.sidedSuccess(pLevel.isClientSide());
  }

  @Override
  protected void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {

    if (!(pEntity instanceof ServerPlayer player) || !(pLevel instanceof ServerLevel level)) {
      return;
    }

    BlockPos master = masterPos(pPos, pState);
    if (player.getBoundingBox().intersects(travelArea(master, pState.getValue(FACING)))) {
      PortalTeleporter.tryTeleport(player, level, master, level.getBlockState(master));
    }
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {

    return isMaster(blockState) ? new PortalBlockEntity(blockPos, blockState) : null;
  }

  // endregion server
}
