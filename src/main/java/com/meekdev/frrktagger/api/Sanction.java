package com.meekdev.frrktagger.api;

import org.jetbrains.annotations.Nullable;

public record Sanction(String type, @Nullable String reason, long date, @Nullable Long expiresAt, boolean revoked) {
    public boolean active() {
        if (revoked) return false;
        return expiresAt == null || expiresAt * 1000L > System.currentTimeMillis();
    }

    public boolean blacklist() {
        return "BLACKLIST".equalsIgnoreCase(type);
    }
}
