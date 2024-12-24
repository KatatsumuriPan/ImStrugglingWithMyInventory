package kpan.kuso.iswmi;

import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import kpan.kuso.iswmi.client.BattleState;
import kpan.kuso.iswmi.network.ModNetworkRegistry;

public final class ModMain {

    public static void init() {
        ClientPlayerEvent.CLIENT_PLAYER_JOIN.register(player -> {
            BattleState.INSTANCE.init();
        });
        ClientPlayerEvent.CLIENT_PLAYER_RESPAWN.register((oldPlayer, newPlayer) -> {
            BattleState.INSTANCE.init();
        });
        ClientTickEvent.CLIENT_LEVEL_POST.register(BattleState.INSTANCE::ticking);
        ClientGuiEvent.RENDER_HUD.register(BattleState.INSTANCE::render);
        ModNetworkRegistry.register();
    }
}
