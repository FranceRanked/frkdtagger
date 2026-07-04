package com.meekdev.frrktagger.icon;

import java.util.Map;
import java.util.Optional;

public final class KitIcons {
    private static final Map<String, Character> GLYPHS = Map.ofEntries(
            Map.entry("crystal", ''),
            Map.entry("sword", ''),
            Map.entry("uhc", ''),
            Map.entry("nethpot", ''),
            Map.entry("pot", ''),
            Map.entry("smp", ''),
            Map.entry("axe", ''),
            Map.entry("diasmp", ''),
            Map.entry("mace", '')
    );

    private KitIcons() {
    }

    public static Optional<Character> glyph(String slug) {
        return slug == null ? Optional.empty() : Optional.ofNullable(GLYPHS.get(slug.toLowerCase()));
    }
}
