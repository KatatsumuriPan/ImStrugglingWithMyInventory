package kpan.kuso.iswmi.network;

import dev.architectury.networking.NetworkManager;
import kpan.kuso.iswmi.EnumState;
import kpan.kuso.iswmi.ItemDropper;
import kpan.kuso.iswmi.ModReference;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class ModNetworkRegistry {

    public static final Identifier PACKET_ID = new Identifier(ModReference.MOD_ID, "1");

    public static void register() {
        // 受け取り側のsideで登録する（s2cならclientsideで十分）
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, PACKET_ID, (buf, context) -> {
            PlayerEntity player = context.getPlayer();
            EnumState enumState = buf.readEnumConstant(EnumState.class);
            ItemDropper.drop(player, enumState);
        });
    }
}
