package com.wiredtext;

import net.fabricmc.api.ModInitializer;

public class WiredTextMod implements ModInitializer {

    @Override
    public void onInitialize() {
        WiredTextServer.register();
    }
}
