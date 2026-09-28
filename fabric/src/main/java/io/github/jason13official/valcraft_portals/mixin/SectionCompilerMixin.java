package io.github.jason13official.valcraft_portals.mixin;

import io.github.jason13official.valcraft_portals.impl.common.block.entity.PortalBlockEntity;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SectionCompiler.class)
public class SectionCompilerMixin {

  @Inject(method = "handleBlockEntity", at = @At("HEAD"), cancellable = true)
  private <E extends BlockEntity> void valcraft_portals$renderPortalsOnce(SectionCompiler.Results results, E blockEntity, CallbackInfo ci) {

    if (blockEntity instanceof PortalBlockEntity) {
      results.globalBlockEntities.add(blockEntity);
      ci.cancel();
    }
  }
}
