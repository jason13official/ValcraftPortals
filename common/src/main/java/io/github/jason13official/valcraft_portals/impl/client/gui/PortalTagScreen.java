package io.github.jason13official.valcraft_portals.impl.client.gui;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.jason13official.valcraft_portals.ValcraftPortalsClient;
import io.github.jason13official.valcraft_portals.impl.common.block.entity.PortalBlockEntity;
import io.github.jason13official.valcraft_portals.impl.network.packet.SetPortalTagC2SPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class PortalTagScreen extends Screen {

  private static final Component TITLE = Component.translatable("screen.valcraft_portals.tag");
  private static final Component HINT = Component.translatable("screen.valcraft_portals.tag.hint");
  private static final Component INFO = Component.translatable("screen.valcraft_portals.tag.info");

  private final BlockPos pos;
  private String value;
  private EditBox tagBox;

  public PortalTagScreen(BlockPos pos, String tag) {
    super(TITLE);
    this.pos = pos;
    this.value = tag;
  }

  @Override
  protected void init() {

    int cx = this.width / 2;
    int cy = this.height / 2;

    this.tagBox = new EditBox(this.font, cx - 75, cy - 10, 150, 20, TITLE);
    this.tagBox.setMaxLength(PortalBlockEntity.MAX_TAG_LENGTH);
    this.tagBox.setValue(this.value);
    this.tagBox.setHint(HINT);
    this.tagBox.setResponder(text -> this.value = text);
    this.addRenderableWidget(this.tagBox);
    this.setInitialFocus(this.tagBox);

    this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> this.onDone()).bounds(cx - 75, cy + 16, 72, 20).build());
    this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, button -> this.onClose()).bounds(cx + 3, cy + 16, 72, 20).build());
  }

  private void onDone() {

    ValcraftPortalsClient.c2s.accept(new SetPortalTagC2SPacket(this.pos, this.value));
    this.onClose();
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {

    if (keyCode == InputConstants.KEY_RETURN || keyCode == InputConstants.KEY_NUMPADENTER) {
      this.onDone();
      return true;
    }

    return super.keyPressed(keyCode, scanCode, modifiers);
  }

  @Override
  public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

    super.render(guiGraphics, mouseX, mouseY, partialTick);

    guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 40, 0xFFFFFF);
    guiGraphics.drawCenteredString(this.font, INFO, this.width / 2, this.height / 2 - 26, 0xA0A0A0);
  }

  @Override
  public boolean isPauseScreen() {
    return false;
  }
}
