package io.github.jason13official.valcraft_portals.impl.common.portal;

import io.github.jason13official.valcraft_portals.impl.common.block.PortalBlock;
import io.github.jason13official.valcraft_portals.impl.common.config.ServerConfig;
import io.github.jason13official.valcraft_portals.impl.common.registry.ModTags;
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
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class PortalTeleporter {

  private static final int COOLDOWN = 40;
  private static final double EXIT_DISTANCE = 1.2;
  private static final int MAX_CONTAINER_DEPTH = 4;

  public static void tryTeleport(ServerPlayer player, ServerLevel level, BlockPos master, BlockState state) {

    if (player.isOnPortalCooldown() || player.isPassenger() || !(state.getBlock() instanceof PortalBlock portal)) {
      return;
    }

    PortalNetwork network = PortalNetwork.get(level.getServer());
    Optional<GlobalPos> partner = network.partner(GlobalPos.of(level.dimension(), master));
    if (partner.isEmpty()) {
      return;
    }

    if (portal.restrictsItems() && ServerConfig.RESTRICT_ITEMS.get() && carriesRestricted(player)) {
      warn(player, Component.translatable("message.valcraft_portals.restricted").withStyle(ChatFormatting.RED));
      player.setPortalCooldown(COOLDOWN);
      return;
    }

    ServerLevel destination = level.getServer().getLevel(partner.get().dimension());
    if (destination == null || (destination != level && !ServerConfig.ALLOW_CROSS_DIMENSION.get())) {
      warn(player, Component.translatable("message.valcraft_portals.cross_dimension").withStyle(ChatFormatting.RED));
      player.setPortalCooldown(COOLDOWN);
      return;
    }

    BlockPos target = partner.get().pos();
    BlockState targetState = destination.getBlockState(target);
    if (!(targetState.getBlock() instanceof PortalBlock) || !PortalBlock.isMaster(targetState)) {
      network.remove(level.getServer(), partner.get());
      return;
    }

    Direction sourceFacing = state.getValue(PortalBlock.FACING);
    Vec3 sourceCenter = Vec3.atBottomCenterOf(master);
    Vec3 offset = player.position().subtract(sourceCenter);
    boolean front = offset.x * sourceFacing.getStepX() + offset.z * sourceFacing.getStepZ() >= 0;

    Direction exit = front ? targetState.getValue(PortalBlock.FACING) : targetState.getValue(PortalBlock.FACING).getOpposite();
    Vec3 arrival = Vec3.atBottomCenterOf(target).add(exit.getStepX() * EXIT_DISTANCE, 0.0, exit.getStepZ() * EXIT_DISTANCE);

    level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.8F, 0.6F);

    player.setPortalCooldown(COOLDOWN);
    player.teleportTo(destination, arrival.x, arrival.y, arrival.z, exit.toYRot(), player.getXRot());
    player.setPortalCooldown(COOLDOWN);

    destination.playSound(null, arrival.x, arrival.y, arrival.z, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.8F, 0.6F);
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
