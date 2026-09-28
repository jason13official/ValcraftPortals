package io.github.jason13official.valcraft_portals.impl.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.jason13official.valcraft_portals.ValcraftPortals;
import net.minecraft.Util;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;

public class PortalTravelScreen extends Screen {

  private static final ResourceLocation FIRE = ValcraftPortals.id("textures/gui/portal_travel_fire.png");
  private static final ResourceLocation CORE = ValcraftPortals.id("textures/gui/portal_travel_core.png");

  private static final int TEXTURE_SIZE = 256;
  private static final long MIN_DURATION_MS = 3000L;
  private static final long FADE_MS = 400L;
  private static final long TIMEOUT_MS = 30000L;

  private final long openedAt = Util.getMillis();
  private long arrivedAt = -1L;
  private long closingAt = -1L;
  private boolean finished;

  public PortalTravelScreen() {
    super(GameNarrator.NO_TITLE);
  }

  public void arrive() {
    if (arrivedAt < 0L) {
      arrivedAt = Util.getMillis();
    }
  }

  public boolean isFinished() {
    return finished;
  }

  @Override
  public void tick() {

    long now = Util.getMillis();

    if (closingAt < 0L && arrivedAt >= 0L && now - openedAt >= MIN_DURATION_MS && (isWorldReady() || now - arrivedAt > TIMEOUT_MS)) {
      closingAt = now;
    }

    if (closingAt >= 0L && now - closingAt >= FADE_MS) {
      finished = true;
      onClose();
    }
  }

  private boolean isWorldReady() {

    if (minecraft == null || minecraft.player == null || minecraft.level == null) {
      return true;
    }

    BlockPos pos = minecraft.player.blockPosition();
    if (minecraft.level.isOutsideBuildHeight(pos.getY()) || minecraft.player.isSpectator() || !minecraft.player.isAlive()) {
      return true;
    }

    for (int dx = -16; dx <= 16; dx += 16) {
      for (int dz = -16; dz <= 16; dz += 16) {
        if (!minecraft.levelRenderer.isSectionCompiled(pos.offset(dx, 0, dz))) {
          return false;
        }
      }
    }

    return true;
  }

  private float opacity(long now) {

    float in = Mth.clamp((now - openedAt) / (float) FADE_MS, 0.0F, 1.0F);
    float out = closingAt < 0L ? 1.0F : 1.0F - Mth.clamp((now - closingAt) / (float) FADE_MS, 0.0F, 1.0F);
    return in * out;
  }

  @Override
  public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

    guiGraphics.fill(0, 0, this.width, this.height, FastColor.ARGB32.color((int) (255 * opacity(Util.getMillis())), 0, 0, 0));
  }

  @Override
  public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

    super.render(guiGraphics, mouseX, mouseY, partialTick);

    long now = Util.getMillis();
    float seconds = (now - openedAt) / 1000.0F;
    float alpha = opacity(now);
    float size = Math.min(this.width, this.height) * 0.9F;
    float pulse = 1.0F + 0.03F * Mth.sin(seconds * 2.5F);

    RenderSystem.enableBlend();
    RenderSystem.defaultBlendFunc();
    guiGraphics.setColor(1.0F, 1.0F, 1.0F, alpha);

    layer(guiGraphics, FIRE, size * pulse, seconds * 25.0F);
    layer(guiGraphics, FIRE, size * 0.8F / pulse, -seconds * 40.0F + 45.0F);
    layer(guiGraphics, CORE, size * pulse, -seconds * 15.0F);

    guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    RenderSystem.disableBlend();
  }

  private void layer(GuiGraphics guiGraphics, ResourceLocation texture, float size, float degrees) {

    PoseStack pose = guiGraphics.pose();
    pose.pushPose();
    pose.translate(this.width / 2.0F, this.height / 2.0F, 0.0F);
    pose.mulPose(Axis.ZP.rotationDegrees(degrees));
    pose.scale(size / TEXTURE_SIZE, size / TEXTURE_SIZE, 1.0F);
    guiGraphics.blit(texture, -TEXTURE_SIZE / 2, -TEXTURE_SIZE / 2, 0.0F, 0.0F, TEXTURE_SIZE, TEXTURE_SIZE, TEXTURE_SIZE, TEXTURE_SIZE);
    pose.popPose();
  }

  @Override
  public boolean shouldCloseOnEsc() {
    return false;
  }

  @Override
  protected boolean shouldNarrateNavigation() {
    return false;
  }

  @Override
  public boolean isPauseScreen() {
    return false;
  }
}
