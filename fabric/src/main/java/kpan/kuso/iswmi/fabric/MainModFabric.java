package kpan.kuso.iswmi.fabric;

import kpan.kuso.iswmi.ModMain;
import net.fabricmc.api.ModInitializer;

public final class MainModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        ModMain.init();
    }
}
