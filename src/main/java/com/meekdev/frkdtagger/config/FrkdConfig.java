package com.meekdev.frkdtagger.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.meekdev.frkdtagger.FrkdTagger;
import net.minecraft.client.Minecraft;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class FrkdConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public enum HighestMode {
        ALWAYS, FALLBACK, NEVER
    }

    private String apiBaseUrl = "https://franceranked.fr";
    private String apiPath = "/api/v1";
    private boolean enabled = true;

    private boolean showInPlayerList = true;
    private boolean showInNametag = true;
    private boolean colorNameByTier = false;
    private boolean showKitIcon = true;
    private boolean showSanctionWarning = true;
    private boolean chatTags = true;
    private boolean showPopup = true;

    private String gameMode = "sword";
    private HighestMode highestMode = HighestMode.FALLBACK;

    private int cacheTtlSeconds = 300;
    private float popupWidth = 2.5F;
    private float popupOffset = 0.0F;
    private boolean autoUpdate = true;
    private String updateRepo = "FranceRanked/frkdtagger";
    private List<String> enabledServers = new ArrayList<>();

    private Map<Integer, Integer> tierColors = defaultTierColors();
    private int retiredColor = 0x9CA3AF;

    private static Map<Integer, Integer> defaultTierColors() {
        Map<Integer, Integer> colors = new LinkedHashMap<>();
        colors.put(1, 0xFBBF24);
        colors.put(2, 0xA78BFA);
        colors.put(3, 0xFB923C);
        colors.put(4, 0x34D399);
        colors.put(5, 0x94A3B8);
        return colors;
    }

    public float popupWidth() {
        return popupWidth <= 0 ? 1.1F : popupWidth;
    }

    public void setPopupWidth(float value) {
        this.popupWidth = value;
    }

    public float popupOffset() {
        return popupOffset;
    }

    public void setPopupOffset(float value) {
        this.popupOffset = value;
    }

    public boolean autoUpdate() {
        return autoUpdate;
    }

    public void setAutoUpdate(boolean value) {
        this.autoUpdate = value;
    }

    public String updateRepo() {
        return updateRepo == null ? "" : updateRepo.trim();
    }

    public void setUpdateRepo(String value) {
        this.updateRepo = value;
    }

    public String apiBaseUrl() {
        String url = apiBaseUrl == null ? "" : apiBaseUrl.trim();
        while (url.endsWith("/")) url = url.substring(0, url.length() - 1);
        if (!url.isEmpty() && !url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
        }
        return url;
    }

    public void setApiBaseUrl(String value) {
        this.apiBaseUrl = value;
        save();
    }

    public String apiPath() {
        String path = apiPath == null ? "" : apiPath.trim();
        while (path.endsWith("/")) path = path.substring(0, path.length() - 1);
        if (!path.isEmpty() && !path.startsWith("/")) path = "/" + path;
        return path;
    }

    public void setApiPath(String value) {
        this.apiPath = value;
        save();
    }

    public boolean enabled() {
        return enabled;
    }

    public void setEnabled(boolean value) {
        this.enabled = value;
        save();
    }

    public boolean showInPlayerList() {
        return showInPlayerList;
    }

    public void setShowInPlayerList(boolean value) {
        this.showInPlayerList = value;
        save();
    }

    public boolean showInNametag() {
        return showInNametag;
    }

    public void setShowInNametag(boolean value) {
        this.showInNametag = value;
        save();
    }

    public boolean colorNameByTier() {
        return colorNameByTier;
    }

    public void setColorNameByTier(boolean value) {
        this.colorNameByTier = value;
        save();
    }

    public boolean showKitIcon() {
        return showKitIcon;
    }

    public void setShowKitIcon(boolean value) {
        this.showKitIcon = value;
        save();
    }

    public boolean showSanctionWarning() {
        return showSanctionWarning;
    }

    public void setShowSanctionWarning(boolean value) {
        this.showSanctionWarning = value;
        save();
    }

    public boolean chatTags() {
        return chatTags;
    }

    public void setChatTags(boolean value) {
        this.chatTags = value;
        save();
    }

    public boolean showPopup() {
        return showPopup;
    }

    public void setShowPopup(boolean value) {
        this.showPopup = value;
        save();
    }

    public String gameMode() {
        return gameMode;
    }

    public void setGameMode(String value) {
        this.gameMode = value;
        save();
    }

    public HighestMode highestMode() {
        return highestMode == null ? HighestMode.FALLBACK : highestMode;
    }

    public void setHighestMode(HighestMode value) {
        this.highestMode = value;
        save();
    }

    public int cacheTtlSeconds() {
        return Math.max(30, cacheTtlSeconds);
    }

    public void setCacheTtlSeconds(int value) {
        this.cacheTtlSeconds = value;
        save();
    }

    public List<String> enabledServers() {
        return enabledServers == null ? List.of() : enabledServers;
    }

    public int tierColor(int tier) {
        if (tierColors == null) return 0xD3D3D3;
        return tierColors.getOrDefault(tier, 0xD3D3D3);
    }

    public void setTierColor(int tier, int color) {
        if (tierColors == null) tierColors = defaultTierColors();
        tierColors.put(tier, color);
        save();
    }

    public int retiredColor() {
        return retiredColor;
    }

    public void setRetiredColor(int value) {
        this.retiredColor = value;
        save();
    }

    private static Path path() {
        return Minecraft.getInstance().gameDirectory.toPath().resolve("config").resolve("frkdtagger.json");
    }

    public static FrkdConfig load() {
        Path path = path();
        try {
            if (Files.exists(path)) {
                String json = Files.readString(path, StandardCharsets.UTF_8);
                FrkdConfig config = GSON.fromJson(json, FrkdConfig.class);
                if (config != null) return config;
            }
        } catch (IOException | RuntimeException e) {
            FrkdTagger.LOGGER.warn("Could not read config, using defaults", e);
        }
        FrkdConfig config = new FrkdConfig();
        config.save();
        return config;
    }

    public void save() {
        Path path = path();
        try {
            Files.createDirectories(path.getParent());
            Files.writeString(path, GSON.toJson(this), StandardCharsets.UTF_8);
        } catch (IOException e) {
            FrkdTagger.LOGGER.warn("Could not save config", e);
        }
    }
}
