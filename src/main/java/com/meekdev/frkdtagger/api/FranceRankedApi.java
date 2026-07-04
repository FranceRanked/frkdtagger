package com.meekdev.frkdtagger.api;

import com.google.gson.reflect.TypeToken;
import com.meekdev.frkdtagger.FrkdTagger;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class FranceRankedApi {
    private static final Type TIER_LIST =
            new TypeToken<List<PlayerTier>>() {}.getType();
    private static final Type MODE_LIST =
            new TypeToken<List<Mode>>() {}.getType();

    private FranceRankedApi() {
    }

    public static CompletableFuture<Profile> profile(UUID uuid) {
        return get("/profiles/" + uuid, Profile.class);
    }

    public static CompletableFuture<List<PlayerTier>> rankings(UUID uuid) {
        return get("/profiles/" + uuid + "/rankings", TIER_LIST);
    }

    public static CompletableFuture<Profile> profileByName(String name) {
        return get("/profiles/by-name/" + encode(name), Profile.class);
    }

    public static CompletableFuture<List<Mode>> modes() {
        return get("/modes/", MODE_LIST);
    }

    public static CompletableFuture<byte[]> cardBytes(UUID uuid) {
        URI uri = URI.create(FrkdTagger.config().apiBaseUrl() + FrkdTagger.config().apiPath() + "/render/profile/" + uuid);
        HttpRequest request = HttpRequest.newBuilder(uri)
                .header("User-Agent", "frkdtagger")
                .header("ngrok-skip-browser-warning", "true")
                .GET()
                .build();

        return FrkdTagger.http()
                .sendAsync(request, HttpResponse.BodyHandlers.ofByteArray())
                .thenApply(response -> {
                    if (response.statusCode() / 100 != 2) {
                        throw new ApiException(response.statusCode(), "/render/profile/" + uuid);
                    }
                    return response.body();
                });
    }

    public static void prewarm(List<String> ids) {
        if (ids.isEmpty()) return;
        String body = FrkdTagger.gson().toJson(Map.of("ids", ids));
        URI uri = URI.create(FrkdTagger.config().apiBaseUrl() + FrkdTagger.config().apiPath() + "/render/prewarm");
        HttpRequest request = HttpRequest.newBuilder(uri)
                .header("Content-Type", "application/json")
                .header("User-Agent", "frkdtagger")
                .header("ngrok-skip-browser-warning", "true")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        FrkdTagger.http().sendAsync(request, HttpResponse.BodyHandlers.discarding());
    }

    private static <T> CompletableFuture<T> get(String path, Type type) {
        URI uri = URI.create(FrkdTagger.config().apiBaseUrl() + FrkdTagger.config().apiPath() + path);
        HttpRequest request = HttpRequest.newBuilder(uri)
                .header("Accept", "application/json")
                .header("User-Agent", "frkdtagger")
                .header("ngrok-skip-browser-warning", "true")
                .GET()
                .build();

        return FrkdTagger.http()
                .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() / 100 != 2) {
                        throw new ApiException(response.statusCode(), path);
                    }
                    return FrkdTagger.gson().<T>fromJson(response.body(), type);
                });
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    public static final class ApiException extends RuntimeException {
        public ApiException(int status, String path) {
            super("FranceRanked API returned " + status + " for " + path);
        }
    }
}
