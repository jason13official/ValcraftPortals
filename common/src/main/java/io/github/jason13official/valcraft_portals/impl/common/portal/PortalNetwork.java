package io.github.jason13official.valcraft_portals.impl.common.portal;

import io.github.jason13official.valcraft_portals.Constants;
import io.github.jason13official.valcraft_portals.impl.common.block.PortalBlock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;

public class PortalNetwork extends SavedData {

  private static final String NAME = Constants.MOD_ID + "_network";

  private final Map<String, List<GlobalPos>> byTag = new LinkedHashMap<>();
  private final Map<GlobalPos, String> tagOf = new HashMap<>();

  public static PortalNetwork get(MinecraftServer server) {

    return server.overworld().getDataStorage().computeIfAbsent(PortalNetwork::load, PortalNetwork::new, NAME);
  }

  public Optional<String> tagOf(GlobalPos portal) {

    return Optional.ofNullable(tagOf.get(portal));
  }

  public Optional<GlobalPos> partner(GlobalPos portal) {

    String tag = tagOf.get(portal);
    if (tag == null) {
      return Optional.empty();
    }

    List<GlobalPos> portals = byTag.get(tag);
    int index = portals.indexOf(portal);
    if (index < 0 || index > 1 || portals.size() < 2) {
      return Optional.empty();
    }

    return Optional.of(portals.get(1 - index));
  }

  public boolean isOverflowing(GlobalPos portal) {

    String tag = tagOf.get(portal);
    return tag != null && byTag.get(tag).indexOf(portal) > 1;
  }

  public void register(MinecraftServer server, GlobalPos portal, String tag) {

    String previous = tagOf.get(portal);
    if (tag.equals(previous)) {
      return;
    }

    if (previous != null) {
      unlink(portal, previous);
    }

    byTag.computeIfAbsent(tag, t -> new ArrayList<>()).add(portal);
    tagOf.put(portal, tag);
    setDirty();

    if (previous != null) {
      refresh(server, previous);
    }
    refresh(server, tag);
  }

  public void remove(MinecraftServer server, GlobalPos portal) {

    String previous = tagOf.get(portal);
    if (previous == null) {
      return;
    }

    unlink(portal, previous);
    setDirty();
    refresh(server, previous);
  }

  private void unlink(GlobalPos portal, String tag) {

    List<GlobalPos> portals = byTag.get(tag);
    if (portals != null) {
      portals.remove(portal);
      if (portals.isEmpty()) {
        byTag.remove(tag);
      }
    }
    tagOf.remove(portal);
  }

  private void refresh(MinecraftServer server, String tag) {

    List<GlobalPos> portals = byTag.get(tag);
    if (portals == null) {
      return;
    }

    for (GlobalPos portal : List.copyOf(portals)) {
      ServerLevel level = server.getLevel(portal.dimension());
      if (level != null && level.isLoaded(portal.pos())) {
        refreshLit(level, portal.pos());
      }
    }
  }

  public void refreshLit(ServerLevel level, BlockPos pos) {

    BlockState state = level.getBlockState(pos);
    if (!(state.getBlock() instanceof PortalBlock) || !PortalBlock.isMaster(state)) {
      return;
    }

    boolean linked = partner(GlobalPos.of(level.dimension(), pos)).isPresent();
    if (state.getValue(PortalBlock.LIT) != linked) {
      level.setBlock(pos, state.setValue(PortalBlock.LIT, linked), 3);
    }
  }

  private static PortalNetwork load(CompoundTag tag) {

    PortalNetwork network = new PortalNetwork();
    ListTag portals = tag.getList("Portals", Tag.TAG_COMPOUND);

    for (int i = 0; i < portals.size(); i++) {
      CompoundTag entry = portals.getCompound(i);
      String portalTag = entry.getString("Tag");
      GlobalPos.CODEC.parse(NbtOps.INSTANCE, entry.get("Pos")).resultOrPartial(Constants.LOG::error).ifPresent(pos -> {
        network.byTag.computeIfAbsent(portalTag, t -> new ArrayList<>()).add(pos);
        network.tagOf.put(pos, portalTag);
      });
    }

    return network;
  }

  @Override
  public CompoundTag save(CompoundTag tag) {

    ListTag portals = new ListTag();

    byTag.forEach((portalTag, positions) -> positions.forEach(pos -> GlobalPos.CODEC.encodeStart(NbtOps.INSTANCE, pos).resultOrPartial(Constants.LOG::error)
        .ifPresent(encoded -> {
          CompoundTag entry = new CompoundTag();
          entry.putString("Tag", portalTag);
          entry.put("Pos", encoded);
          portals.add(entry);
        })));

    tag.put("Portals", portals);
    return tag;
  }

}
