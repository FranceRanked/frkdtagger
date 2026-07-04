package com.meekdev.frkdtagger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.meekdev.frkdtagger.config.FrkdConfig;
import com.meekdev.frkdtagger.tier.TierCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.HttpClient;
import java.time.Duration;

public final class FrkdTagger {
    public static final String MOD_ID = "frkdtagger";
    public static final Logger LOGGER = LoggerFactory.getLogger("FRKD");

    private static final Gson GSON = new GsonBuilder().create();
    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private static FrkdConfig config;

    private FrkdTagger() {
    }

    public static void init() {
        config = FrkdConfig.load();
        TierCache.refreshModes();
        LOGGER.info("FRKD EST PREEEET", config.apiBaseUrl());
    }

    public static String cycleGameMode() {
        String next = TierCache.nextMode(config.gameMode());
        config.setGameMode(next);
        return next;
    }

    public static void reload() {
        config = FrkdConfig.load();
        TierCache.clear();
        TierCache.refreshModes();
    }

    public static FrkdConfig config() {
        return config;
    }

    public static Gson gson() {
        return GSON;
    }

    public static HttpClient http() {
        return HTTP;
    }
}
