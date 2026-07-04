package com.meekdev.frkdtagger.render;

import com.meekdev.frkdtagger.FrkdTagger;
import com.meekdev.frkdtagger.api.FranceRankedApi;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class CardPrewarm {
    private static final long INTERVAL_MS = 10_000L;
    private static final Set<UUID> done = ConcurrentHashMap.newKeySet();
    private static long lastMs;

    private CardPrewarm() {
    }

    public static void tick() {
        if (!FrkdTagger.config().enabled() || !FrkdTagger.config().showPopup()) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.getConnection() == null) {
            done.clear();
            return;
        }
        long now = System.currentTimeMillis();
        if (now - lastMs < INTERVAL_MS) return;
        lastMs = now;

        List<String> fresh = new ArrayList<>();
        for (PlayerInfo info : mc.getConnection().getOnlinePlayers()) {
            UUID id = info.getProfile().id();
            if (id != null && done.add(id)) {
                fresh.add(id.toString());
            }
        }
        if (!fresh.isEmpty()) {
            FranceRankedApi.prewarm(fresh);
        }
    }
}
