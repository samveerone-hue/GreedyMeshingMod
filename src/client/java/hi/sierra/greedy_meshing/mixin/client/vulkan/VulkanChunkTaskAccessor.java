package hi.sierra.greedy_meshing.mixin.client.vulkan;

//? if VULKANMOD {
import net.vulkanmod.render.chunk.RenderSection;
import net.vulkanmod.render.chunk.build.task.ChunkTask;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * {@code section} is declared on {@code ChunkTask} (the parent of {@code BuildTask}); Mixin can't
 * {@code @Shadow} an inherited field from the subclass, so read it via an accessor on the declaring
 * class — the same pattern the Sodium path uses for {@code ChunkBuilderTask.render}.
 */
@Mixin(ChunkTask.class)
public interface VulkanChunkTaskAccessor {
    @Accessor("section")
    RenderSection greedyMeshing$getSection();
}
//?}
