package com.meekdev.frrktagger.tier;

import com.meekdev.frrktagger.FrrkTagger;
import com.meekdev.frrktagger.api.FranceRankedApi;
import com.meekdev.frrktagger.api.Mode;
import com.meekdev.frrktagger.api.Profile;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public final class TierCache {
    private static final ConcurrentHashMap<UUID, Entry> PLAYERS = new ConcurrentHashMap<>();
    private static final Set<UUID> IN_FLIGHT = ConcurrentHashMap.newKeySet();
    private static final CopyOnWriteArrayList<Mode> MODES = new CopyOnWriteArrayList<>();

    private TierCache() {
    }

    private static final class Entry {
        volatile Profile profile;
        volatile long fetchedAt;

        Entry(Profile profile, long fetchedAt) {
            this.profile = profile;
            this.fetchedAt = fetchedAt;
        }
    }

    public static void refreshModes() {
        FranceRankedApi.modes()
                .thenAccept(modes -> {
                    MODES.clear();
                    if (modes != null) MODES.addAll(modes);
                    FrrkTagger.LOGGER.info("Loaded {} FranceRanked modes", MODES.size());
                })
                .exceptionally(t -> {
                    FrrkTagger.LOGGER.warn("Failed to load FranceRanked modes", t);
                    return null;
                });
    }

    public static List<Mode> modes() {
        return MODES;
    }

    public static Optional<Mode> findMode(String slug) {
        return MODES.stream().filter(m -> m.slug().equalsIgnoreCase(slug)).findFirst();
    }

    public static String nextMode(String current) {
        if (MODES.isEmpty()) return current;
        int index = -1;
        for (int i = 0; i < MODES.size(); i++) {
            if (MODES.get(i).slug().equalsIgnoreCase(current)) {
                index = i;
                break;
            }
        }
        return MODES.get((index + 1) % MODES.size()).slug();
    }

    public static Optional<Profile> profile(UUID uuid) {
        if (uuid == null || uuid.version() != 4) return Optional.empty();

        Entry entry = PLAYERS.get(uuid);
        if (entry == null) {
            fetch(uuid);
            return Optional.empty();
        }
        if (isStale(entry)) fetch(uuid);
        return Optional.ofNullable(entry.profile);
    }

    private static boolean isStale(Entry entry) {
        long ttl = FrrkTagger.config().cacheTtlSeconds() * 1000L;
        return System.currentTimeMillis() - entry.fetchedAt >= ttl;
    }

    private static void fetch(UUID uuid) {
        if (!IN_FLIGHT.add(uuid)) return;
        FranceRankedApi.profile(uuid).whenComplete((profile, t) -> {
            IN_FLIGHT.remove(uuid);
            long now = System.currentTimeMillis();
            if (profile != null) {
                PLAYERS.compute(uuid, (k, existing) -> {
                    if (existing == null) return new Entry(profile, now);
                    existing.profile = profile;
                    existing.fetchedAt = now;
                    return existing;
                });
            } else {
                PLAYERS.putIfAbsent(uuid, new Entry(null, now));
            }
        });
    }

    public static void clear() {
        PLAYERS.clear();
    }
}
