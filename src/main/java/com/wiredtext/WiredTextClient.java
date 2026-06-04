package com.wiredtext;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class WiredTextClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(WiredTextSyncPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                boolean wasActive = WiredTextState.active;

                WiredTextState.active = payload.active();
                WiredTextState.overdrive = payload.overdrive();
                WiredTextState.minDelayMs = payload.minDelayMs();
                WiredTextState.maxDelayMs = payload.maxDelayMs();

                if (payload.active() && !wasActive) {
                    WiredTextState.scheduleNext();
                }
                if (!payload.active()) {
                    WiredTextState.nextTriggerTime = 0;
                    WiredTextState.showUntil = 0;
                }
            });
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            WiredTextState.tick();
        });

        WiredTextOverlay.register();
    }
}
