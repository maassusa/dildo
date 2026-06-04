package com.wiredtext;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class WiredTextServer {

    public static boolean active = false;
    public static boolean overdrive = false;
    public static long minDelayMs = 60_000L;
    public static long maxDelayMs = 360_000L;

    public static void register() {
        // Register payload type
        PayloadTypeRegistry.playS2C().register(WiredTextSyncPayload.ID, WiredTextSyncPayload.CODEC);

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("wt")
                .requires(src -> src.hasPermissionLevel(2))
                .then(CommandManager.literal("on")
                    .executes(ctx -> {
                        active = true;
                        overdrive = false;
                        broadcastState(ctx.getSource().getServer());
                        ctx.getSource().sendFeedback(() -> Text.literal("§7[WiredText] Включён для всех."), true);
                        return 1;
                    }))
                .then(CommandManager.literal("off")
                    .executes(ctx -> {
                        active = false;
                        overdrive = false;
                        broadcastState(ctx.getSource().getServer());
                        ctx.getSource().sendFeedback(() -> Text.literal("§7[WiredText] Выключен для всех."), true);
                        return 1;
                    }))
                .then(CommandManager.literal("rate")
                    .then(CommandManager.argument("min", FloatArgumentType.floatArg(0.1f, 999f))
                        .then(CommandManager.argument("max", FloatArgumentType.floatArg(0.1f, 999f))
                            .executes(ctx -> {
                                float min = FloatArgumentType.getFloat(ctx, "min");
                                float max = FloatArgumentType.getFloat(ctx, "max");
                                if (min >= max) {
                                    ctx.getSource().sendFeedback(() -> Text.literal("§cМинимум должен быть меньше максимума!"), false);
                                    return 0;
                                }
                                minDelayMs = (long)(min * 1000f);
                                maxDelayMs = (long)(max * 1000f);
                                broadcastState(ctx.getSource().getServer());
                                ctx.getSource().sendFeedback(() -> Text.literal("§7[WiredText] Интервал: " + min + "–" + max + " сек."), true);
                                return 1;
                            }))))
                .then(CommandManager.literal("overdrive")
                    .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                        .executes(ctx -> {
                            overdrive = BoolArgumentType.getBool(ctx, "enabled");
                            if (overdrive) active = true;
                            broadcastState(ctx.getSource().getServer());
                            ctx.getSource().sendFeedback(() -> Text.literal("§7[WiredText] Overdrive: " + overdrive), true);
                            return 1;
                        })))
            );
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            sendStateTo(handler.player);
        });
    }

    public static void broadcastState(MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            sendStateTo(player);
        }
    }

    public static void sendStateTo(ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, new WiredTextSyncPayload(active, overdrive, minDelayMs, maxDelayMs));
    }
}
