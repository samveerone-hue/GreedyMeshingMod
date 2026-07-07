package hi.sierra.greedy_meshing.mixin.client.sodium;

//? if SODIUM {
import net.caffeinemc.mods.sodium.client.render.chunk.RenderSection;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.tasks.ChunkBuilderTask;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChunkBuilderTask.class)
public interface SodiumChunkBuilderTaskAccessor {
    // Sodium renamed this field render -> section between 0.8.x (used through 26.1.x) and 0.9.x
    // (26.2). A wrong name here fails SILENTLY (defaultRequire=0) and only surfaces as an
    // AbstractMethodError at runtime the first time a chunk-builder thread calls the accessor.
    //? if >=26.2 {
    /*@Accessor("section")
    *///?} else {
    @Accessor("render")
    //?}
    RenderSection greedyMeshing$getRender();
}
//?}
