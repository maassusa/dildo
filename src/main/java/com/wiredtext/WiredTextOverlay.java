package com.wiredtext;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class WiredTextOverlay {

    public static void register() {
        HudRenderCallback.EVENT.register((drawContext, tickDeltaManager) -> {
            if (!WiredTextState.isShowing()) return;

            MinecraftClient client = MinecraftClient.getInstance();
            if (client == null || client.player == null) return;

            TextRenderer textRenderer = client.textRenderer;
            int screenWidth = client.getWindow().getScaledWidth();
            int screenHeight = client.getWindow().getScaledHeight();

            String text = WiredTextState.currentText;

            // Compute pixel position from relative floats
            int textWidth = textRenderer.getWidth(text);
            int x = (int)(WiredTextState.textX * screenWidth) - textWidth / 2;
            int y = (int)(WiredTextState.textY * screenHeight);

            // Clamp to screen
            x = Math.max(2, Math.min(x, screenWidth - textWidth - 2));
            y = Math.max(2, Math.min(y, screenHeight - 10));

            // No fade — appear and disappear instantly
            // A=55 out of 255 → very transparent grey
            int color = (55 << 24) | (0xAAAAAA);

            // Slightly randomize size by using shadow = false, no shadow
            drawContext.drawText(textRenderer, text, x, y, color, false);
        });
    }
}
