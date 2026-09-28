package io.github.jason13official.valcraft_portals.impl.common.portal;

import io.github.jason13official.valcraft_portals.impl.common.block.PortalBlock;
import io.github.jason13official.valcraft_portals.impl.common.config.ServerConfig;
import io.github.jason13official.valcraft_portals.impl.common.registry.ModTags;
import io.github.jason13official.valcraft_portals.impl.network.packet.PortalTravelS2CPacket;
import io.github.jason13official.valcraft_portals.platform.Services;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;

public class PortalTeleporter {

  private static final int COOLDOWN = 40;
  private static final double EXIT_DISTANCE = 1.2;
  private static final int MAX_CONTAINER_DEPTH = 4;

  public static boolean canEnter(ServerPlayer player, ServerLevel level, BlockPos master, BlockState state) {

    if (player.isOnPortalCooldown() || !player.canUsePortal(false) || !(state.getBlock() instanceof PortalBlock portal)) {
      return false;
    }

    Optional<GlobalPos> partner = PortalNetwork.get(level.getServer()).partner(GlobalPos.of(level.dimension(), master));
    if (partner.isEmpty()) {
      return false;
    }

    if (portal.restrictsItems() && ServerConfig.RESTRICT_ITEMS.get() && carriesRestricted(player)) {
      warn(player, Component.translatable("message.valcraft_portals.restricted").withStyle(ChatFormatting.RED));
      player.setPortalCooldown(COOLDOWN);
      return false;
    }

    ServerLevel destination = level.getServer().getLevel(partner.get().dimension());
    if (destination == null || (destination != level && !ServerConfig.ALLOW_CROSS_DIMENSION.get())) {
      warn(player, Component.translatable("message.valcraft_portals.cross_dimension").withStyle(ChatFormatting.RED));
      player.setPortalCooldown(COOLDOWN);
      return false;
    }

    return true;
  }

  public static DimensionTransition destination(ServerLevel level, Entity entity, BlockPos master) {

    BlockState state = level.getBlockState(master);
    if (!(state.getBlock() instanceof PortalBlock)) {
      return null;
    }

    PortalNetwork network = PortalNetwork.get(level.getServer());
    Optional<GlobalPos> partner = network.partner(GlobalPos.of(level.dimension(), master));
    if (partner.isEmpty()) {
      return null;
    }

    ServerLevel destination = level.getServer().getLevel(partner.get().dimension());
    if (destination == null) {
      return null;
    }

    BlockPos target = partner.get().pos();
    BlockState targetState = destination.getBlockState(target);
    if (!(targetState.getBlock() instanceof PortalBlock) || !PortalBlock.isMaster(targetState)) {
      network.remove(level.getServer(), partner.get());
      return null;
    }

    Direction sourceFacing = state.getValue(PortalBlock.FACING);
    Vec3 offset = entity.position().subtract(Vec3.atBottomCenterOf(master));
    boolean front = offset.x * sourceFacing.getStepX() + offset.z * sourceFacing.getStepZ() >= 0;

    Direction exit = front ? targetState.getValue(PortalBlock.FACING) : targetState.getValue(PortalBlock.FACING).getOpposite();
    Vec3 arrival = Vec3.atBottomCenterOf(target).add(exit.getStepX() * EXIT_DISTANCE, 0.0, exit.getStepZ() * EXIT_DISTANCE);

    level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.8F, 0.6F);

    if (entity instanceof ServerPlayer player) {
      Services.PLATFORM.sendToPlayer(player, new PortalTravelS2CPacket(false));
    }

    return new DimensionTransition(destination, arrival, Vec3.ZERO, exit.toYRot(), entity.getXRot(), DimensionTransition.PLACE_PORTAL_TICKET.then(arrived -> {
      arrived.setPortalCooldown(COOLDOWN);
      arrived.level().playSound(null, arrived.getX(), arrived.getY(), arrived.getZ(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.8F, 0.6F);

      if (arrived instanceof ServerPlayer player) {
        Services.PLATFORM.sendToPlayer(player, new PortalTravelS2CPacket(true));
      }
    }));
  }

  public static boolean carriesRestricted(Player player) {

    Inventory inventory = player.getInventory();
    for (int i = 0; i < inventory.getContainerSize(); i++) {
      if (isRestricted(inventory.getItem(i), 0)) {
        return true;
      }
    }

    return isRestricted(player.containerMenu.getCarried(), 0);
  }

  private static boolean isRestricted(ItemStack stack, int depth) {

    if (stack.isEmpty()) {
      return false;
    }
    if (stack.is(ModTags.PORTAL_RESTRICTED)) {
      return true;
    }
    if (depth >= MAX_CONTAINER_DEPTH) {
      return false;
    }

    ItemContainerContents container = stack.get(DataComponents.CONTAINER);
    if (container != null) {
      for (ItemStack inner : container.nonEmptyItems()) {
        if (isRestricted(inner, depth + 1)) {
          return true;
        }
      }
    }

    BundleContents bundle = stack.get(DataComponents.BUNDLE_CONTENTS);
    if (bundle != null) {
      for (ItemStack inner : bundle.items()) {
        if (isRestricted(inner, depth + 1)) {
          return true;
        }
      }
    }

    return false;
  }

  private static void warn(ServerPlayer player, Component message) {

    player.connection.send(new ClientboundSetTitlesAnimationPacket(5, 40, 10));
    player.connection.send(new ClientboundSetSubtitleTextPacket(message));
    player.connection.send(new ClientboundSetTitleTextPacket(Component.empty()));
  }
}
