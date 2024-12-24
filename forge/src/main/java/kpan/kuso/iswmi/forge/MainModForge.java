package kpan.kuso.iswmi.forge;

import dev.architectury.platform.forge.EventBuses;
import kpan.kuso.iswmi.ModMain;
import kpan.kuso.iswmi.ModReference;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ModReference.MOD_ID)
public final class MainModForge {
    public MainModForge() {
        // Submit our event bus to let Architectury API register our content on the right time.
        EventBuses.registerModEventBus(ModReference.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        // Run our common setup.
        ModMain.init();
    }
}
