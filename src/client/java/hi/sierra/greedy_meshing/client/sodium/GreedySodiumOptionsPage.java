package hi.sierra.greedy_meshing.client.sodium;

//? if <1.21.11 {
import com.google.common.collect.ImmutableList;
import net.caffeinemc.mods.sodium.client.gui.options.OptionFlag;
import net.caffeinemc.mods.sodium.client.gui.options.OptionGroup;
import net.caffeinemc.mods.sodium.client.gui.options.OptionImpl;
import net.caffeinemc.mods.sodium.client.gui.options.OptionPage;
import net.caffeinemc.mods.sodium.client.gui.options.control.TickBoxControl;
import net.caffeinemc.mods.sodium.client.gui.options.storage.OptionStorage;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import hi.sierra.greedy_meshing.GreedyConfig;

public final class GreedySodiumOptionsPage {
    private GreedySodiumOptionsPage() {
    }

    public static OptionPage create() {
        OptionStorage<GreedyConfig.Data> storage = new OptionStorage<>() {
            private GreedyConfig.Data data;

            @Override
            public GreedyConfig.Data getData() {
                if (data == null) data = GreedyConfig.snapshot();
                return data;
            }

            @Override
            public void save() {
                if (data != null) {
                    GreedyConfig.apply(data);
                    data = null;
                }
            }
        };

        OptionGroup group = OptionGroup.createBuilder()
                .add(OptionImpl.<GreedyConfig.Data, Boolean>createBuilder(Boolean.class, storage)
                        .setName(Component.literal("Enabled"))
                        .setTooltip(Component.literal("Enable greedy meshing. When off, vanilla chunk rendering is used."))
                        .setBinding((d, v) -> d.enabled = v, d -> d.enabled)
                        .setControl(TickBoxControl::new)
                        .setFlags(OptionFlag.REQUIRES_RENDERER_RELOAD)
                        .build())
                .add(OptionImpl.<GreedyConfig.Data, Boolean>createBuilder(Boolean.class, storage)
                        .setName(Component.literal("Aggressive Greedy (Absolute)"))
                        .setTooltip(Component.literal("Merge same-block faces ignoring AO boundaries. Fewer quads; slightly coarser lighting on large surfaces."))
                        .setBinding((d, v) -> d.aggressiveGreedy = v, d -> d.aggressiveGreedy)
                        .setControl(TickBoxControl::new)
                        .setFlags(OptionFlag.REQUIRES_RENDERER_RELOAD)
                        .build())
                .add(OptionImpl.<GreedyConfig.Data, Boolean>createBuilder(Boolean.class, storage)
                        .setName(Component.literal("Greedy Water (Flat Surfaces)"))
                        .setTooltip(FabricLoader.getInstance().isModLoaded("vulkanmod")
                                ? Component.literal("Not supported on VulkanMod: translucency depth-sort breaks with large merged water quads.")
                                : Component.literal("Merge flat still-water faces into larger quads. EXPERIMENTAL — some surfaces may render with missing or black faces."))
                        .setBinding((d, v) -> d.greedyWater = v, d -> d.greedyWater)
                        .setControl(TickBoxControl::new)
                        .setEnabled(() -> !FabricLoader.getInstance().isModLoaded("vulkanmod"))
                        .setFlags(OptionFlag.REQUIRES_RENDERER_RELOAD)
                        .build())
                .build();

        return new OptionPage(Component.literal("Greedy Meshing"), ImmutableList.of(group));
    }
}
//?}
