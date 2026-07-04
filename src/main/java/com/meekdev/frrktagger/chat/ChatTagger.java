package com.meekdev.frrktagger.chat;

import com.meekdev.frrktagger.FrrkTagger;
import com.meekdev.frrktagger.hud.TierDisplay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;

import java.util.UUID;

public final class ChatTagger {
    private ChatTagger() {
    }

    public static Component tag(Component message) {
        if (!FrrkTagger.config().enabled() || !FrrkTagger.config().chatTags()) return message;

        Minecraft mc = Minecraft.getInstance();
        if (mc.getConnection() == null) return message;

        String text = message.getString();
        for (PlayerInfo info : mc.getConnection().getOnlinePlayers()) {
            String name =
                    //? if >=1.21.11 {
                    info.getProfile().name();
                    //?} else {
                    /*info.getProfile().getName();
                    *///?}
            if (name == null || name.isBlank() || !containsWord(text, name)) continue;

            UUID uuid =
                    //? if >=1.21.11 {
                    info.getProfile().id();
                    //?} else {
                    /*info.getProfile().getId();
                    *///?}

            String playerName = name;
            return TierDisplay.chatPrefix(uuid)
                    .map(prefix -> (Component) Component.empty().append(prefix).append(message)
                            .withStyle(style -> style
                                    .withClickEvent(new ClickEvent.RunCommand("/frkd card " + playerName))
                                    .withHoverEvent(new HoverEvent.ShowText(
                                            Component.literal("Voir la carte de " + playerName)))))
                    .orElse(message);
        }
        return message;
    }

    private static boolean containsWord(String text, String name) {
        int index = text.indexOf(name);
        while (index >= 0) {
            boolean leftOk = index == 0 || !Character.isLetterOrDigit(text.charAt(index - 1));
            int end = index + name.length();
            boolean rightOk = end >= text.length() || !Character.isLetterOrDigit(text.charAt(end));
            if (leftOk && rightOk) return true;
            index = text.indexOf(name, index + 1);
        }
        return false;
    }
}
