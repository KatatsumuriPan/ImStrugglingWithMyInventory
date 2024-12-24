package kpan.kuso.iswmi.client;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import kpan.kuso.iswmi.EnumState;
import kpan.kuso.iswmi.ItemDropper;
import kpan.kuso.iswmi.network.ModNetworkRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.ColorHelper.Argb;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;

public class BattleState {
    public static final BattleState INSTANCE = new BattleState();
    public static final int INTERVAL = 60 * 20;

    @Environment(EnvType.CLIENT)
    private static final RenderLayer DROP_METER = RenderLayer.of(
            "drop_meter",
            VertexFormats.POSITION_COLOR,
            DrawMode.TRIANGLE_FAN,
            256,
            RenderLayer.MultiPhaseParameters.builder().program(RenderLayer.GUI_PROGRAM).transparency(RenderLayer.TRANSLUCENT_TRANSPARENCY).depthTest(RenderLayer.LEQUAL_DEPTH_TEST).build(false)
    );

    private EnumState enumState = EnumState.DISABLED;
    private int timer = INTERVAL;
    private int failedTimer = 0;

    public void init() {
        enumState = EnumState.ROW1;
        timer = INTERVAL + 200;
    }

    public void ticking(ClientWorld world) {
        if (enumState == EnumState.DISABLED)
            return;
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null || player.isDead())
            return;
        if (timer % 20 == 0) {
            if (timer / 20 <= 5 && willDropItem(player))
                player.playSound(enumState.sound(), 10F, (float) Math.pow(2, (5 - timer / 20 + 1) / 12.0));
        }
        if (--timer <= 0) {
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeEnumConstant(enumState);
            NetworkManager.sendToServer(ModNetworkRegistry.PACKET_ID, buf);
            if (willDropItem(player)) {
                failedTimer = 20;
                player.playSound(enumState.sound(), 10, (float) Math.pow(2, 6 / 12.0));
                player.playSound(enumState.sound(), 10, (float) Math.pow(2, 6 / 12.0 - 1));
            }
            enumState = enumState.next();
            timer = INTERVAL;
        }
        if (failedTimer > 0)
            failedTimer--;
    }

    @Environment(EnvType.CLIENT)
    public void render(DrawContext drawContext, float tickDelta) {
        if (failedTimer > 0) {
            renderFan(drawContext, 17, 17, 15, 0xFF00_0000, 1, MathHelper.PI * 2);
            renderFan(drawContext, 17, 17, 14F, 0xFF22_2222, 1, MathHelper.PI * 2);
            renderFan(drawContext, 17, 17, 14F, 0xFFFF_3333, 1, MathHelper.PI * 2);
            drawContext.drawText(MinecraftClient.getInstance().textRenderer, "0.00", 32, 2, 0xFFFF_3333, true);
        } else {
            float timerInterpolated = timer + 1 - tickDelta;
            boolean willDrop = willDropItem(MinecraftClient.getInstance().player);
            float alpha = willDrop ? (float) (Math.cos(timerInterpolated * 0.2) * 0.2F + 0.6F) : 0.4F;
            if (timer <= INTERVAL) {
                float scale = willDrop ? (MathHelper.clamp((float) (INTERVAL - timerInterpolated) / INTERVAL * 100 - 90F, 0.4F, 1F)) : 0.4F;
                renderFan(drawContext, 17 * scale, 17 * scale, 15 * scale, 0x00_0000, 1, MathHelper.PI * 2);
                renderFan(drawContext, 17 * scale, 17 * scale, 14F * scale, 0x22_2222, 1, MathHelper.PI * 2);
                renderFan(drawContext, 17 * scale, 17 * scale, 14F * scale, enumState.color(), alpha, MathHelper.PI * 2 * (timerInterpolated / INTERVAL));
                if (timer / 20.0F <= 5.2F && willDrop)
                    drawContext.drawText(MinecraftClient.getInstance().textRenderer, String.format("%.2f", Math.min(timerInterpolated / 20.0F, 5)), 32, 2, 0xFFFF_FFFF, true);
            } else {
                float scale = 0.4F;
                float wholeAlpha = (1 - (timer - INTERVAL) / 200F);
                renderFan(drawContext, 17 * scale, 17 * scale, 15 * scale, 0x000000, wholeAlpha, MathHelper.PI * 2);
                renderFan(drawContext, 17 * scale, 17 * scale, 14F * scale, 0x222222, wholeAlpha, MathHelper.PI * 2);
                renderFan(drawContext, 17 * scale, 17 * scale, 14F * scale, enumState.color(), wholeAlpha * alpha, MathHelper.PI * 2);
            }
        }
    }


    private boolean willDropItem(ClientPlayerEntity player) {
        boolean dropped = false;
        for (Integer index : ItemDropper.getSlots(enumState)) {
            ItemStack itemStack = player.getInventory().getStack(index);
            if (!itemStack.isEmpty()) {
                dropped = true;
                break;
            }
        }
        return dropped;
    }

    private static void renderFan(DrawContext drawContext, float centerX, float centerY, float radius, int color, float alpha, float theta) {
        Matrix4f matrix4f = drawContext.getMatrices().peek().getPositionMatrix();

        float r = Argb.getRed(color) / 255.0F;
        float g = Argb.getGreen(color) / 255.0F;
        float b = Argb.getBlue(color) / 255.0F;
        VertexConsumer vertexConsumer = drawContext.getVertexConsumers().getBuffer(DROP_METER);
        vertexConsumer.vertex(matrix4f, centerX, centerY, 0).color(r, g, b, alpha).next();
        for (float t = theta; t >= 0; t -= 0.2F) {
            float x = centerX + MathHelper.sin(t) * radius;
            float y = centerY - MathHelper.cos(t) * radius;
            vertexConsumer.vertex(matrix4f, x, y, 0).color(r, g, b, alpha).next();
        }
        vertexConsumer.vertex(matrix4f, centerX, centerY - radius, 0).color(r, g, b, alpha).next();
        drawContext.draw();
    }

}
