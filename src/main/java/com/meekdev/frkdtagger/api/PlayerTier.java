package com.meekdev.frkdtagger.api;

import org.jetbrains.annotations.Nullable;

public record PlayerTier(
        int tier,
        String position,
        boolean isRetired,
        @Nullable Integer peakTier,
        @Nullable String peakPosition,
        long assignedAt,
        @Nullable Mode mode
) {
    public boolean isHigh() {
        return "high".equalsIgnoreCase(position);
    }

    public boolean hasPeak() {
        return peakTier != null && peakPosition != null;
    }

    public boolean peakIsHigh() {
        return "high".equalsIgnoreCase(peakPosition);
    }

    public int rank() {
        return tier * 2 + (isHigh() ? 0 : 1);
    }

    public int peakRank() {
        if (!hasPeak()) return Integer.MAX_VALUE;
        return peakTier * 2 + (peakIsHigh() ? 0 : 1);
    }
}
