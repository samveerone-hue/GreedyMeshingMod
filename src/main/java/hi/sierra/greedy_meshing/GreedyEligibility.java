package hi.sierra.greedy_meshing;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class GreedyEligibility {
    private static final ConcurrentHashMap<BlockState, Boolean> CACHE = new ConcurrentHashMap<>();

    /**
     * "Fancy grass" / connected-grass mods (e.g. BetterGrassify, LambdaBetterGrass) rewrite a
     * block's side quads based on neighbouring blocks — the grass-top texture wraps down the sides.
     * Greedy meshing intercepts these blocks before those mods run and re-emits them from the
     * static baked model, silently discarding the effect (see issue #2). When such a mod is present
     * we leave the affected blocks to the normal render path so the mod can do its job.
     */
    private static final Set<String> FANCY_GRASS_MOD_IDS = Set.of(
            "bettergrass",        // BetterGrassify (its mod id is "bettergrass", not the slug)
            "lambdabettergrass",  // LambdaBetterGrass (and the "Refabricated" fork — same mod id)
            "ardagrass"           // ArdaGrass
    );

    private static final boolean FANCY_GRASS_MOD_PRESENT = fancyGrassModPresent();

    /** Full-cube vanilla blocks whose sides fancy-grass mods rewrite from neighbours. */
    private static final Set<Block> FANCY_GRASS_BLOCKS = Set.of(
            Blocks.GRASS_BLOCK,
            Blocks.PODZOL,
            Blocks.MYCELIUM,
            Blocks.CRIMSON_NYLIUM,
            Blocks.WARPED_NYLIUM
    );

    private GreedyEligibility() {
    }

    private static boolean fancyGrassModPresent() {
        FabricLoader loader = FabricLoader.getInstance();
        for (String id : FANCY_GRASS_MOD_IDS) {
            if (loader.isModLoaded(id)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isGreedyOpaqueCube(BlockState state, BlockGetter level, BlockPos pos) {
        Boolean cached = CACHE.get(state);
        if (cached != null) {
            return cached;
        }
        boolean result = !state.isAir()
                && state.getRenderShape() == RenderShape.MODEL
                && state.getFluidState().isEmpty()
                && !state.hasBlockEntity()
                //? if >=1.21.2 {
                && state.isSolidRender()
                //?} else {
                /*&& state.isSolidRender(level, pos)
                *///?}
                && state.isCollisionShapeFullBlock(level, pos)
                && !(FANCY_GRASS_MOD_PRESENT && FANCY_GRASS_BLOCKS.contains(state.getBlock()));
        CACHE.put(state, result);
        return result;
    }

    /** Clear the eligibility cache (e.g. on resource reload). */
    public static void clearCache() {
        CACHE.clear();
    }
}
