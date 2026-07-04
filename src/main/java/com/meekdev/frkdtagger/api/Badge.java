package com.meekdev.frkdtagger.api;

import org.jetbrains.annotations.Nullable;

public record Badge(String slug, String name, @Nullable String icon, @Nullable String color) {
    public int colorRgb(int fallback) {
        if (color == null) return fallback;
        try {
            return Integer.parseInt(color.replace("#", "").trim(), 16);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    public record Awarded(long awardedAt, @Nullable Badge badge) {
    }
}
