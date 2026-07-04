package com.meekdev.frrktagger;

import com.meekdev.frrktagger.api.FranceRankedApi;
import com.meekdev.frrktagger.config.gui.ConfigScreen;
import com.meekdev.frrktagger.input.Keybinds;
import com.meekdev.frrktagger.render.CardPrewarm;
import com.meekdev.frrktagger.render.CardScreen;
import com.meekdev.frrktagger.render.InfoPanel;
import com.meekdev.frrktagger.render.ProfilePopupRenderer;
import com.meekdev.frrktagger.update.FrrkUpdater;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;
//? if fabric {
//? if >=26 {
/*import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
*///?} else {
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
//?}
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
//? if >=26 {
/*import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
*///?} else {
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
//?}
//?}
//? if neoforge {
/*import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;
*///?}

final class FrrkClientSetup {
    private FrrkClientSetup() {
    }

    //? if >=26 {
    /*private static final KeyMapping.Category CATEGORY =
            KeyMapping.Category.register(Identifier.fromNamespaceAndPath(FrrkTagger.MOD_ID, "frkd"));
    *///?}

    private static KeyMapping key(String translation) {
        //? if >=26 {
        /*return new KeyMapping(translation, InputConstants.UNKNOWN.getValue(), CATEGORY);
        *///?} elif >=1.21.11 {
        return new KeyMapping(translation, InputConstants.UNKNOWN.getValue(), KeyMapping.Category.MISC);
        //?} else {
        /*return new KeyMapping(translation, InputConstants.UNKNOWN.getValue(), "key.categories.misc");
        *///?}
    }

    private static void createKeys() {
        Keybinds.popup = key("key.frrktagger.popup");
        Keybinds.config = key("key.frrktagger.config");
        Keybinds.cycle = key("key.frrktagger.cycle");
    }

    static void handleTick() {
        Minecraft mc = Minecraft.getInstance();
        CardPrewarm.tick();
        if (Keybinds.cycle != null) {
            while (Keybinds.cycle.consumeClick()) {
                String mode = FrrkTagger.cycleGameMode();
                if (mc.player != null) {
                    mc.gui./*? if v262 {*//*hud.setOverlayMessage*//*?} else {*/ setOverlayMessage /*?}*/(Component.literal("FRKD: " + mode), false);
                }
            }
        }
        if (Keybinds.config != null && Keybinds.config.consumeClick()) {
            mc./*? if v262 {*//*gui.setScreen*//*?} else {*/ setScreen /*?}*/(new ConfigScreen(mc./*? if v262 {*//*gui.screen()*//*?} else {*/ screen /*?}*/));
        }
    }

    //? if fabric {
    static void setupFabric() {
        createKeys();
        //? if >=26 {
        /*KeyMappingHelper.registerKeyMapping(Keybinds.popup);
        KeyMappingHelper.registerKeyMapping(Keybinds.config);
        KeyMappingHelper.registerKeyMapping(Keybinds.cycle);
        *///?} else {
        KeyBindingHelper.registerKeyBinding(Keybinds.popup);
        KeyBindingHelper.registerKeyBinding(Keybinds.config);
        KeyBindingHelper.registerKeyBinding(Keybinds.cycle);
        //?}
        ClientTickEvents.END_CLIENT_TICK.register(client -> handleTick());
        //? if >=26 {
        /*LevelRenderEvents.AFTER_TRANSLUCENT_FEATURES.register(ProfilePopupRenderer::renderWorld);
        *///?} else {
        WorldRenderEvents.AFTER_ENTITIES.register(ProfilePopupRenderer::renderWorld);
        //?}
        registerCommands();
        FrrkUpdater.checkAsync();
        ScreenEvents.AFTER_INIT.register((client, screen, w, h) -> promptUpdate(client, screen));
    }

    private static boolean updatePrompted;

    private static void promptUpdate(Minecraft client, Screen screen) {
        FrrkUpdater.Release rel = FrrkUpdater.available();
        if (updatePrompted || rel == null || !(screen instanceof TitleScreen)) return;
        updatePrompted = true;
        open(client, new ConfirmScreen(
                yes -> {
                    if (yes) FrrkUpdater.download(rel);
                    open(client, screen);
                },
                Component.literal("FRKD update " + rel.version()),
                Component.literal("Version " + rel.version() + " available. Download now? (applied on next launch)")));
    }

    private static void open(Minecraft client, Screen screen) {
        //? if v262 {
        /*client.gui.setScreen(screen);
        *///?} else {
        client.setScreen(screen);
        //?}
    }

    private static LiteralArgumentBuilder<FabricClientCommandSource> lit(String name) {
        //? if >=26 {
        /*return ClientCommands.literal(name);
        *///?} else {
        return ClientCommandManager.literal(name);
        //?}
    }

    private static <T> RequiredArgumentBuilder<FabricClientCommandSource, T> arg(String name, ArgumentType<T> type) {
        //? if >=26 {
        /*return ClientCommands.argument(name, type);
        *///?} else {
        return ClientCommandManager.argument(name, type);
        //?}
    }

    private static void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, access) ->
                dispatcher.register(lit("frkd")
                        .then(lit("reload").executes(ctx -> {
                            FrrkTagger.reload();
                            ctx.getSource().sendFeedback(Component.literal("FRKD reloaded"));
                            return 1;
                        }))
                        .then(lit("toggle").executes(ctx -> {
                            boolean on = !FrrkTagger.config().enabled();
                            FrrkTagger.config().setEnabled(on);
                            ctx.getSource().sendFeedback(Component.literal("FRKD " + (on ? "enabled" : "disabled")));
                            return 1;
                        }))
                        .then(lit("kit")
                                .then(arg("kit", StringArgumentType.word()).executes(ctx -> {
                                    String kit = StringArgumentType.getString(ctx, "kit");
                                    FrrkTagger.config().setGameMode(kit);
                                    ctx.getSource().sendFeedback(Component.literal("Displayed kit: " + kit));
                                    return 1;
                                })))
                        .then(lit("card")
                                .then(arg("name", StringArgumentType.word()).executes(ctx -> {
                                    openCard(StringArgumentType.getString(ctx, "name"));
                                    return 1;
                                })))
                        .then(lit("profile")
                                .then(arg("name", StringArgumentType.word()).executes(ctx -> {
                                    String name = StringArgumentType.getString(ctx, "name");
                                    ctx.getSource().sendFeedback(Component.literal("Looking up " + name + "..."));
                                    FranceRankedApi.profileByName(name).whenComplete((profile, t) -> Minecraft.getInstance().execute(() -> {
                                        Minecraft mc = Minecraft.getInstance();
                                        if (mc.player == null) return;
                                        if (profile == null) {
                                            sendMsg(mc.player, Component.literal("No FranceRanked profile for " + name));
                                            return;
                                        }
                                        InfoPanel.lines(profile).forEach(line -> sendMsg(mc.player, line));
                                    }));
                                    return 1;
                                })))));
    }

    private static void openCard(String name) {
        Minecraft mc = Minecraft.getInstance();
        UUID online = resolveUuid(name);
        if (online != null) {
            mc.execute(() -> mc./*? if v262 {*//*gui.setScreen*//*?} else {*/ setScreen /*?}*/(new CardScreen(online, name)));
            return;
        }
        FranceRankedApi.profileByName(name).whenComplete((profile, t) -> mc.execute(() -> {
            if (mc.player == null) return;
            if (profile == null) {
                sendMsg(mc.player, Component.literal("No FranceRanked profile for " + name));
                return;
            }
            mc./*? if v262 {*//*gui.setScreen*//*?} else {*/ setScreen /*?}*/(new CardScreen(parseUuid(profile.uuid()), name));
        }));
    }

    private static void sendMsg(Player player, Component msg) {
        //? if >=26 {
        /*player.sendSystemMessage(msg);
        *///?} else {
        player.displayClientMessage(msg, false);
        //?}
    }

    private static UUID resolveUuid(String name) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getConnection() == null) return null;
        for (PlayerInfo info : mc.getConnection().getOnlinePlayers()) {
            if (name.equalsIgnoreCase(info.getProfile().name())) return info.getProfile().id();
        }
        return null;
    }

    private static UUID parseUuid(String value) {
        String hex = value.replace("-", "");
        if (hex.length() != 32) return UUID.fromString(value);
        return UUID.fromString(hex.replaceFirst("(.{8})(.{4})(.{4})(.{4})(.{12})", "$1-$2-$3-$4-$5"));
    }
    //?}

    //? if neoforge {
    /*static void setupNeoforge(IEventBus modBus) {
        createKeys();
        modBus.addListener((RegisterKeyMappingsEvent event) -> {
            event.register(Keybinds.popup);
            event.register(Keybinds.config);
            event.register(Keybinds.cycle);
        });
        NeoForge.EVENT_BUS.addListener((ClientTickEvent.Post event) -> handleTick());
    }
    *///?}
}
