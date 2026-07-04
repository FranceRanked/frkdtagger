package com.meekdev.frrktagger.api;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public record Profile(
        String id,
        String uuid,
        String username,
        @Nullable String region,
        int overallPoints,
        int rank,
        List<PlayerTier> tiers,
        List<Sanction> sanctions,
        List<Badge.Awarded> badges
) {
    public List<PlayerTier> tiersOrEmpty() {
        return tiers == null ? List.of() : tiers;
    }

    public List<Sanction> sanctionsOrEmpty() {
        return sanctions == null ? List.of() : sanctions;
    }

    public List<Badge.Awarded> badgesOrEmpty() {
        return badges == null ? List.of() : badges;
    }

    public Optional<Sanction> activeSanction() {
        return sanctionsOrEmpty().stream().filter(Sanction::active).findFirst();
    }
}
