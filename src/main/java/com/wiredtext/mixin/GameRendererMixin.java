package com.wiredtext.mixin;

import com.wiredtext.WiredTextState;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.util.math.MatrixStack;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.text.Text;
import net.minecraft.text.Style;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    // Not injecting here — HUD rendering is done via HudRenderCallback registered in the overlay class
}
