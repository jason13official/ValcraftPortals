package io.github.jason13official.valcraft_portals.mixin;

import io.github.jason13official.valcraft_portals.impl.common.block.entity.PortalBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.renderer.chunk.ChunkRenderDispatcher$RenderChunk$RebuildTask")
public class RebuildTaskMixin {

  @Inject(method = "handleBlockEntity", at = @At("HEAD"), cancellable = true)
  private void valcraft_portals$renderPortalsOnce(@Coerce Object compileResults, BlockEntity blockEntity, CallbackInfo ci) {

    if (blockEntity instanceof PortalBlockEntity) {
      ((CompileResultsAccessor) compileResults).valcraft_portals$getGlobalBlockEntities().add(blockEntity);
      ci.cancel();
    }
  }
}
