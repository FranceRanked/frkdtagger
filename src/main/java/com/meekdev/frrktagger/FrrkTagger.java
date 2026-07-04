package com.meekdev.frrktagger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.meekdev.frrktagger.config.FrrkConfig;
import com.meekdev.frrktagger.tier.TierCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.HttpClient;
import java.time.Duration;

public final class FrrkTagger {
    public static final String MOD_ID = "frrktagger";
    public static final Logger LOGGER = LoggerFactory.getLogger("FRKD");

    private static final Gson GSON = new GsonBuilder().create();
    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private static FrrkConfig config;

    private FrrkTagger() {
    }

    public static void init() {
        config = FrrkConfig.load();
        TierCache.refreshModes();
        LOGGER.info("FRKD EST PREEEET", config.apiBaseUrl());
    }

    public static String cycleGameMode() {
        String next = TierCache.nextMode(config.gameMode());
        config.setGameMode(next);
        return next;
    }

    public static void reload() {
        config = FrrkConfig.load();
        TierCache.clear();
        TierCache.refreshModes();
    }

    public static FrrkConfig config() {
        return config;
    }

    public static Gson gson() {
        return GSON;
    }

    public static HttpClient http() {
        return HTTP;
    }
}
