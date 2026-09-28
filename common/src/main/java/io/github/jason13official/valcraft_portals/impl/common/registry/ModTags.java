package io.github.jason13official.valcraft_portals.impl.common.registry;

import io.github.jason13official.valcraft_portals.ValcraftPortals;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModTags {

  public static final TagKey<Item> PORTAL_RESTRICTED = TagKey.create(Registries.ITEM, ValcraftPortals.id("portal_restricted"));
}
