package hi.sierra.greedy_meshing.mixin.client.sodium;

//? if <1.21.11 {
import net.caffeinemc.mods.sodium.client.gui.SodiumOptionsGUI;
import net.caffeinemc.mods.sodium.client.gui.options.OptionPage;
import net.minecraft.client.gui.screens.Screen;
import hi.sierra.greedy_meshing.client.sodium.GreedySodiumOptionsPage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(SodiumOptionsGUI.class)
public abstract class SodiumOptionsGUIMixin {
    @Shadow private List<OptionPage> pages;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void greedyMeshing$addOptionsPage(Screen prevScreen, CallbackInfo ci) {
        pages.add(GreedySodiumOptionsPage.create());
    }
}
//? } else {
public class SodiumOptionsGUIMixin {
    // No-op on Sodium 0.8+ (1.21.11+): config is registered via the sodium:config_api_user entrypoint instead.
}
//?}
