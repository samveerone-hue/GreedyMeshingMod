package hi.sierra.greedy_meshing.client.sodium;

//? if >=1.21.11 {
/*import net.caffeinemc.mods.sodium.api.config.ConfigEntryPoint;
import net.caffeinemc.mods.sodium.api.config.option.ControlValueFormatter;
import net.caffeinemc.mods.sodium.api.config.option.OptionFlag;
import net.caffeinemc.mods.sodium.api.config.structure.ConfigBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import hi.sierra.greedy_meshing.GreedyConfig;
import hi.sierra.greedy_meshing.GreedyEligibility;

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

        // --- General options ---
        var general = builder.createOptionGroup();

        general.addOption(builder.createBooleanOption(Identifier.fromNamespaceAndPath("greedy_meshing", "enabled"))
                .setName(Component.literal("Enabled"))
                .setTooltip(Component.literal("Enable greedy meshing. When off, the mod does nothing and vanilla chunk rendering is used."))
                .setDefaultValue(true)
                .setBinding(v -> draft[0].enabled = v, () -> draft[0].enabled)
                .setStorageHandler(storage)
                .setFlags(OptionFlag.REQUIRES_RENDERER_RELOAD));

        general.addOption(builder.createBooleanOption(Identifier.fromNamespaceAndPath("greedy_meshing", "aggressive_greedy"))
                .setName(Component.literal("Aggressive Greedy (Absolute)"))
                .setTooltip(Component.literal("Merge same-block faces ignoring AO boundaries. Fewer quads; slightly coarser lighting on large flat surfaces."))
                .setDefaultValue(false)
                .setBinding(v -> draft[0].aggressiveGreedy = v, () -> draft[0].aggressiveGreedy)
                .setStorageHandler(storage)
                .setFlags(OptionFlag.REQUIRES_RENDERER_RELOAD));

        boolean greedyWaterUnsupported = !GreedyEligibility.GREEDY_WATER_SUPPORTED;
        String greedyWaterTooltip = greedyWaterUnsupported && GreedyEligibility.GREEDY_WATER_UNSUPPORTED_TOOLTIP != null
                ? GreedyEligibility.GREEDY_WATER_UNSUPPORTED_TOOLTIP
                : "Merge flat still-water faces into larger quads (open ocean/lake interiors). EXPERIMENTAL — some surfaces may render with missing or black faces.";
        general.addOption(builder.createBooleanOption(Identifier.fromNamespaceAndPath("greedy_meshing", "greedy_water"))
                .setName(Component.literal("Greedy Water (Flat Surfaces)"))
                .setTooltip(Component.literal(greedyWaterTooltip))
                .setDefaultValue(false)
                .setBinding(v -> draft[0].greedyWater = v, () -> draft[0].greedyWater)
                .setStorageHandler(storage)
                .setFlags(OptionFlag.REQUIRES_RENDERER_RELOAD)
                .setEnabled(!greedyWaterUnsupported));

        page.addOptionGroup(general);

        // --- Debug options ---
        var debug = builder.createOptionGroup()
                .setName(Component.literal("Debug"));

        debug.addOption(builder.createBooleanOption(Identifier.fromNamespaceAndPath("greedy_meshing", "debug_wireframe"))
                .setName(Component.literal("Debug Wireframe"))
                .setTooltip(Component.literal("Render a wireframe overlay showing merged quad boundaries."))
                .setDefaultValue(false)
                .setBinding(v -> draft[0].debugWireframe = v, () -> draft[0].debugWireframe)
                .setStorageHandler(storage)
                .setFlags(OptionFlag.REQUIRES_RENDERER_RELOAD));

        debug.addOption(builder.createBooleanOption(Identifier.fromNamespaceAndPath("greedy_meshing", "debug_comparison"))
                .setName(Component.literal("Debug Comparison (Split-Screen)"))
                .setTooltip(Component.literal("Split-screen comparison: left half uses greedy meshing, right half uses vanilla rendering."))
                .setDefaultValue(false)
                .setBinding(v -> draft[0].debugComparison = v, () -> draft[0].debugComparison)
                .setStorageHandler(storage)
                .setFlags(OptionFlag.REQUIRES_RENDERER_RELOAD));

        debug.addOption(builder.createBooleanOption(Identifier.fromNamespaceAndPath("greedy_meshing", "debug_triangles_hud"))
                .setName(Component.literal("Debug Triangles (Overlay)"))
                .setTooltip(Component.literal("Show a HUD overlay with triangle/quad count statistics."))
                .setDefaultValue(false)
                .setBinding(v -> draft[0].debugTrianglesHud = v, () -> draft[0].debugTrianglesHud)
                .setStorageHandler(storage));

        debug.addOption(builder.createIntegerOption(Identifier.fromNamespaceAndPath("greedy_meshing", "mesh_opacity"))
                .setName(Component.literal("Wireframe Opacity"))
                .setTooltip(Component.literal("Opacity of the debug wireframe overlay (0–100%)."))
                .setDefaultValue(35)
                .setRange(0, 100, 1)
                .setValueFormatter(v -> Component.literal(v + "%"))
                .setBinding(v -> draft[0].meshOpacity = v / 100.0f, () -> Math.round(draft[0].meshOpacity * 100))
                .setStorageHandler(storage));

        page.addOptionGroup(debug);

        builder.registerOwnModOptions().addPage(page);
    }
}
*///?} else {
public final class GreedySodiumConfigEntryPoint {
    // Stub for Sodium 0.6/0.7 (1.21 through 1.21.10) — sodium:config_api_user is never invoked on those versions.
}
//?}
