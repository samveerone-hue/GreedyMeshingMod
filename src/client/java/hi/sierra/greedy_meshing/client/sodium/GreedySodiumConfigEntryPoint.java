package hi.sierra.greedy_meshing.client.sodium;

//? if >=1.21.11 {
/*import net.caffeinemc.mods.sodium.api.config.ConfigEntryPoint;
import net.caffeinemc.mods.sodium.api.config.option.OptionFlag;
import net.caffeinemc.mods.sodium.api.config.structure.ConfigBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import hi.sierra.greedy_meshing.GreedyConfig;
import net.fabricmc.loader.api.FabricLoader;

public final class GreedySodiumConfigEntryPoint implements ConfigEntryPoint {
    @Override
    public void registerConfigLate(ConfigBuilder builder) {
        GreedyConfig.Data[] draft = { GreedyConfig.snapshot() };

        var storage = (net.caffeinemc.mods.sodium.api.config.StorageEventHandler) () -> {
            GreedyConfig.apply(draft[0]);
            draft[0] = GreedyConfig.snapshot();
        };

        var page = builder.createOptionPage()
                .setName(Component.literal("Greedy Meshing"));

        page.addOption(builder.createBooleanOption(Identifier.fromNamespaceAndPath("greedy_meshing", "enabled"))
                .setName(Component.literal("Enabled"))
                .setTooltip(Component.literal("Enable greedy meshing. When off, the mod does nothing and vanilla chunk rendering is used."))
                .setBinding(v -> draft[0].enabled = v, () -> draft[0].enabled)
                .setStorageHandler(storage)
                .setFlags(OptionFlag.REQUIRES_RENDERER_RELOAD));

        page.addOption(builder.createBooleanOption(Identifier.fromNamespaceAndPath("greedy_meshing", "aggressive_greedy"))
                .setName(Component.literal("Aggressive Greedy (Absolute)"))
                .setTooltip(Component.literal("Merge same-block faces ignoring AO boundaries. Fewer quads; slightly coarser lighting on large flat surfaces."))
                .setBinding(v -> draft[0].aggressiveGreedy = v, () -> draft[0].aggressiveGreedy)
                .setStorageHandler(storage)
                .setFlags(OptionFlag.REQUIRES_RENDERER_RELOAD));

        boolean greedyWaterUnsupported = FabricLoader.getInstance().isModLoaded("vulkanmod");
        page.addOption(builder.createBooleanOption(Identifier.fromNamespaceAndPath("greedy_meshing", "greedy_water"))
                .setName(Component.literal("Greedy Water (Flat Surfaces)"))
                .setTooltip(greedyWaterUnsupported
                        ? Component.literal("Not supported on VulkanMod: translucency depth-sort breaks with large merged water quads.")
                        : Component.literal("Merge flat still-water faces into larger quads (open ocean/lake interiors). EXPERIMENTAL — some surfaces may render with missing or black faces."))
                .setBinding(v -> draft[0].greedyWater = v, () -> draft[0].greedyWater)
                .setStorageHandler(storage)
                .setFlags(OptionFlag.REQUIRES_RENDERER_RELOAD)
                .setEnabled(!greedyWaterUnsupported));

        builder.registerOwnModOptions().addPage(page);
    }
}
*///?} else {
public final class GreedySodiumConfigEntryPoint {
    // Stub for Sodium 0.6/0.7 (1.21 through 1.21.10) — sodium:config_api_user is never invoked on those versions.
}
//?}
