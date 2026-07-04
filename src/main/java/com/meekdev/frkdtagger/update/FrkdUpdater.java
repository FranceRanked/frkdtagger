package com.meekdev.frkdtagger.update;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.meekdev.frkdtagger.FrkdTagger;
import net.fabricmc.loader.api.FabricLoader;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

public final class FrkdUpdater {
    public record Release(String version, String jarUrl, String jarName) {
    }

    private static volatile Release available;
    private static volatile boolean downloaded;

    private FrkdUpdater() {
    }

    public static Release available() {
        return available;
    }

    public static boolean downloaded() {
        return downloaded;
    }

    public static void checkAsync() {
        if (!FrkdTagger.config().autoUpdate()) return;
        String repo = FrkdTagger.config().updateRepo();
        if (repo.isBlank()) return;

        HttpRequest req = HttpRequest.newBuilder(URI.create("https://api.github.com/repos/" + repo + "/releases/latest"))
                .header("User-Agent", "frkdtagger")
                .header("Accept", "application/vnd.github+json")
                .GET()
                .build();

        FrkdTagger.http().sendAsync(req, HttpResponse.BodyHandlers.ofString()).thenAccept(resp -> {
            if (resp.statusCode() / 100 != 2) return;
            try {
                parse(resp.body());
            } catch (Exception e) {
                FrkdTagger.LOGGER.warn("[frkd] update check failed", e);
            }
        });
    }

    private static void parse(String body) {
        JsonObject json = FrkdTagger.gson().fromJson(body, JsonObject.class);
        String tag = json.get("tag_name").getAsString().replaceFirst("^v", "");
        if (tag.equals(modVersion("frkdtagger"))) return;

        String mc = modVersion("minecraft");
        JsonArray assets = json.getAsJsonArray("assets");
        for (int i = 0; assets != null && i < assets.size(); i++) {
            JsonObject asset = assets.get(i).getAsJsonObject();
            String name = asset.get("name").getAsString();
            if (name.endsWith(".jar") && name.contains(mc)) {
                available = new Release(tag, asset.get("browser_download_url").getAsString(), name);
                FrkdTagger.LOGGER.info("[frkd] update available: {} ({})", tag, name);
                return;
            }
        }
    }

    public static void download(Release rel) {
        Path modsDir = FabricLoader.getInstance().getGameDir().resolve("mods");
        Path target = modsDir.resolve(rel.jarName());
        Path tmp = modsDir.resolve(rel.jarName() + ".part");

        HttpRequest req = HttpRequest.newBuilder(URI.create(rel.jarUrl()))
                .header("User-Agent", "frkdtagger")
                .GET()
                .build();

        FrkdTagger.http().sendAsync(req, HttpResponse.BodyHandlers.ofByteArray()).thenAccept(resp -> {
            if (resp.statusCode() / 100 != 2) return;
            try {
                Files.write(tmp, resp.body());
                Optional<Path> current = currentJar();
                if (current.isPresent() && !current.get().equals(target)) {
                    try {
                        Files.deleteIfExists(current.get());
                    } catch (Exception ignored) {
                    }
                }
                Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING);
                downloaded = true;
                available = null;
                FrkdTagger.LOGGER.info("[frkd] update downloaded: {}", target);
            } catch (Exception e) {
                FrkdTagger.LOGGER.warn("[frkd] update download failed", e);
            }
        });
    }

    private static Optional<Path> currentJar() {
        return FabricLoader.getInstance().getModContainer("frkdtagger")
                .flatMap(m -> m.getOrigin().getPaths().stream().findFirst());
    }

    private static String modVersion(String modId) {
        return FabricLoader.getInstance().getModContainer(modId)
                .map(m -> m.getMetadata().getVersion().getFriendlyString())
                .orElse("");
    }
}
