package io.github.jason13official.valcraft_portals.impl.common.block;

import net.minecraft.util.StringRepresentable;

public enum PortalPart implements StringRepresentable {
  BOTTOM_LEFT("bottom_left", -1, 0),
  BOTTOM("bottom", 0, 0),
  BOTTOM_RIGHT("bottom_right", 1, 0),
  MIDDLE_LEFT("middle_left", -1, 1),
  MIDDLE("middle", 0, 1),
  MIDDLE_RIGHT("middle_right", 1, 1),
  TOP_LEFT("top_left", -1, 2),
  TOP("top", 0, 2),
  TOP_RIGHT("top_right", 1, 2);

  private final String name;
  public final int dx;
  public final int dy;

  PortalPart(String name, int dx, int dy) {
    this.name = name;
    this.dx = dx;
    this.dy = dy;
  }

  public boolean isMaster() {
    return this == BOTTOM;
  }

  public static PortalPart at(int dx, int dy) {
    for (PortalPart part : values()) {
      if (part.dx == dx && part.dy == dy) {
        return part;
      }
    }
    return null;
  }

  @Override
  public String getSerializedName() {
    return name;
  }
}
