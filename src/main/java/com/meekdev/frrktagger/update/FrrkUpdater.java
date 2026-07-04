package com.meekdev.frrktagger.update;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.meekdev.frrktagger.FrrkTagger;
import net.fabricmc.loader.api.FabricLoader;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

public final class FrrkUpdater {
    public record Release(String version, String jarUrl, String jarName) {
    }

    private static volatile Release available;
    private static volatile boolean downloaded;

    private FrrkUpdater() {
    }

    public static Release available() {
        return available;
    }

    public static boolean downloaded() {
        return downloaded;
    }

    public static void checkAsync() {
        if (!FrrkTagger.config().autoUpdate()) return;
        String repo = FrrkTagger.config().updateRepo();
        if (repo.isBlank()) return;

        HttpRequest req = HttpRequest.newBuilder(URI.create("https://api.github.com/repos/" + repo + "/releases/latest"))
                .header("User-Agent", "frkdtagger")
                .header("Accept", "application/vnd.github+json")
                .GET()
                .build();

        FrrkTagger.http().sendAsync(req, HttpResponse.BodyHandlers.ofString()).thenAccept(resp -> {
            if (resp.statusCode() / 100 != 2) return;
            try {
                parse(resp.body());
            } catch (Exception e) {
                FrrkTagger.LOGGER.warn("[frrk] update check failed", e);
            }
        });
    }

    private static void parse(String body) {
        JsonObject json = FrrkTagger.gson().fromJson(body, JsonObject.class);
        String tag = json.get("tag_name").getAsString().replaceFirst("^v", "");
        if (tag.equals(modVersion("frrktagger"))) return;

        String mc = modVersion("minecraft");
        JsonArray assets = json.getAsJsonArray("assets");
        for (int i = 0; assets != null && i < assets.size(); i++) {
            JsonObject asset = assets.get(i).getAsJsonObject();
            String name = asset.get("name").getAsString();
            if (name.endsWith(".jar") && name.contains(mc)) {
                available = new Release(tag, asset.get("browser_download_url").getAsString(), name);
                FrrkTagger.LOGGER.info("[frrk] update available: {} ({})", tag, name);
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

        FrrkTagger.http().sendAsync(req, HttpResponse.BodyHandlers.ofByteArray()).thenAccept(resp -> {
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
                FrrkTagger.LOGGER.info("[frrk] update downloaded: {}", target);
            } catch (Exception e) {
                FrrkTagger.LOGGER.warn("[frrk] update download failed", e);
            }
        });
    }

    private static Optional<Path> currentJar() {
        return FabricLoader.getInstance().getModContainer("frrktagger")
                .flatMap(m -> m.getOrigin().getPaths().stream().findFirst());
    }

    private static String modVersion(String modId) {
        return FabricLoader.getInstance().getModContainer(modId)
                .map(m -> m.getMetadata().getVersion().getFriendlyString())
                .orElse("");
    }
}
