package com.meekdev.frkdtagger.tier;

import com.meekdev.frkdtagger.FrkdTagger;
import com.meekdev.frkdtagger.api.PlayerTier;
import com.meekdev.frkdtagger.config.FrkdConfig;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public final class TierSelector {
    private TierSelector() {
    }

    public static Optional<PlayerTier> select(List<PlayerTier> tiers) {
        if (tiers == null || tiers.isEmpty()) return Optional.empty();

        FrkdConfig config = FrkdTagger.config();
        String slug = config.gameMode();

        Optional<PlayerTier> forMode = tiers.stream()
                .filter(t -> t.mode() != null && t.mode().slug().equalsIgnoreCase(slug))
                .findFirst();

        Optional<PlayerTier> highest = tiers.stream()
                .min(Comparator.comparingInt(PlayerTier::rank));

        return switch (config.highestMode()) {
            case ALWAYS -> highest;
            case NEVER -> forMode;
            case FALLBACK -> forMode.isPresent() ? forMode : highest;
        };
    }
}
