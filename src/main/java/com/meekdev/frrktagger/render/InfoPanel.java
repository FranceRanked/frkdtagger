package com.meekdev.frrktagger.render;

import com.meekdev.frrktagger.api.Badge;
import com.meekdev.frrktagger.api.PlayerTier;
import com.meekdev.frrktagger.api.Profile;
import com.meekdev.frrktagger.api.Sanction;
import com.meekdev.frrktagger.icon.KitIcons;
import com.meekdev.frrktagger.tier.TierFormatter;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class InfoPanel {
    private InfoPanel() {
    }

    public static List<Component> lines(Profile profile) {
        List<Component> lines = new ArrayList<>();

        lines.add(Component.literal(profile.username()).withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD));

        MutableComponent stats = Component.literal("#" + profile.rank()).withStyle(ChatFormatting.GOLD)
                .append(Component.literal(" · ").withStyle(ChatFormatting.DARK_GRAY))
                .append(Component.literal(profile.overallPoints() + " pts").withStyle(ChatFormatting.GRAY));
        if (profile.region() != null && !profile.region().isBlank()) {
            stats.append(Component.literal(" · ").withStyle(ChatFormatting.DARK_GRAY))
                    .append(Component.literal(profile.region()).withStyle(ChatFormatting.AQUA));
        }
        lines.add(stats);

        profile.tiersOrEmpty().stream()
                .sorted(Comparator.comparingInt(PlayerTier::rank))
                .forEach(tier -> lines.add(tierLine(tier)));

        profile.activeSanction().ifPresent(s -> lines.add(sanctionLine(s)));

        String badges = profile.badgesOrEmpty().stream()
                .map(Badge.Awarded::badge)
                .filter(b -> b != null)
                .map(Badge::name)
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
        if (!badges.isBlank()) {
            lines.add(Component.literal(badges).withStyle(ChatFormatting.YELLOW));
        }

        return lines;
    }

    private static Component tierLine(PlayerTier tier) {
        MutableComponent line = Component.empty();
        if (tier.mode() != null) {
            KitIcons.glyph(tier.mode().slug()).ifPresent(glyph ->
                    line.append(Component.literal(glyph + " ").withStyle(s -> s.withColor(0xFFFFFF))));
        }
        line.append(TierFormatter.label(tier));
        if (tier.mode() != null) {
            line.append(Component.literal("  " + tier.mode().name()).withStyle(ChatFormatting.GRAY));
        }
        return line;
    }

    private static Component sanctionLine(Sanction sanction) {
        return Component.literal("⚠ " + sanction.type()).withStyle(s -> s.withColor(0xEF4444));
    }
}
