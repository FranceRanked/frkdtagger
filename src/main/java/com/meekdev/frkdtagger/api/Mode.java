package com.meekdev.frkdtagger.api;

import org.jetbrains.annotations.Nullable;

public record Mode(String slug, String name, @Nullable String color, @Nullable String icon) {
    public static final Mode UNKNOWN = new Mode("unknown", "Unknown", null, null);

    public int colorRgb(int fallback) {
        if (color == null) return fallback;
        try {
            return Integer.parseInt(color.replace("#", "").trim(), 16);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
}
