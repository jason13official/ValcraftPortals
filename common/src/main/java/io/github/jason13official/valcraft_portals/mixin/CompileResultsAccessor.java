package io.github.jason13official.valcraft_portals.mixin;

import java.util.List;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "net.minecraft.client.renderer.chunk.ChunkRenderDispatcher$RenderChunk$RebuildTask$CompileResults")
public interface CompileResultsAccessor {

  @Accessor("globalBlockEntities")
  List<BlockEntity> valcraft_portals$getGlobalBlockEntities();
}
