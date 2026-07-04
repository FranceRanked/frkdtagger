package com.meekdev.frkdtagger.hud;

import com.meekdev.frkdtagger.FrkdTagger;
import com.meekdev.frkdtagger.api.PlayerTier;
import com.meekdev.frkdtagger.api.Profile;
import com.meekdev.frkdtagger.config.FrkdConfig;
import com.meekdev.frkdtagger.icon.KitIcons;
import com.meekdev.frkdtagger.tier.TierCache;
import com.meekdev.frkdtagger.tier.TierFormatter;
import com.meekdev.frkdtagger.tier.TierSelector;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.Optional;
import java.util.UUID;

public final class TierDisplay {
    public static final String WARNING = "⚠ ";

    private TierDisplay() {
    }

    public static Optional<Component> chatPrefix(UUID uuid) {
        FrkdConfig config = FrkdTagger.config();
        if (!config.enabled() || uuid == null) return Optional.empty();

        Optional<Profile> profileOpt = TierCache.profile(uuid);
        if (profileOpt.isEmpty()) return Optional.empty();
        Profile profile = profileOpt.get();

        boolean sanctioned = config.showSanctionWarning() && profile.activeSanction().isPresent();
        Optional<PlayerTier> tierOpt = TierSelector.select(profile.tiersOrEmpty());
        if (!sanctioned && tierOpt.isEmpty()) return Optional.empty();

        MutableComponent prefix = Component.empty();
        if (sanctioned) {
            prefix.append(Component.literal(WARNING).withStyle(s -> s.withColor(0xEF4444)));
        }
        if (tierOpt.isPresent()) {
            PlayerTier tier = tierOpt.get();
            if (config.showKitIcon() && tier.mode() != null) {
                KitIcons.glyph(tier.mode().slug()).ifPresent(glyph ->
                        prefix.append(Component.literal(glyph + " ").withStyle(s -> s.withColor(0xFFFFFF))));
            }
            prefix.append(TierFormatter.label(tier));
        }
        prefix.append(Component.literal(" ").withStyle(ChatFormatting.GRAY));
        return Optional.of(prefix);
    }

    public static Component append(UUID uuid, Component name) {
        FrkdConfig config = FrkdTagger.config();
        if (!config.enabled() || uuid == null) return name;

        Optional<Profile> profileOpt = TierCache.profile(uuid);
        if (profileOpt.isEmpty()) return name;
        Profile profile = profileOpt.get();

        boolean sanctioned = config.showSanctionWarning() && profile.activeSanction().isPresent();
        Optional<PlayerTier> tierOpt = TierSelector.select(profile.tiersOrEmpty());

        if (!sanctioned && tierOpt.isEmpty()) return name;

        MutableComponent prefix = Component.empty();
        if (sanctioned) {
            prefix.append(Component.literal(WARNING).withStyle(s -> s.withColor(0xEF4444)));
        }

        if (tierOpt.isEmpty()) {
            return prefix.append(name);
        }

        PlayerTier tier = tierOpt.get();
        if (config.showKitIcon() && tier.mode() != null) {
            KitIcons.glyph(tier.mode().slug()).ifPresent(glyph ->
                    prefix.append(Component.literal(glyph + " ").withStyle(s -> s.withColor(0xFFFFFF))));
        }

        prefix.append(TierFormatter.label(tier));
        prefix.append(Component.literal(" | ").withStyle(ChatFormatting.GRAY));

        Component tagged = name;
        if (config.colorNameByTier()) {
            int nameColor = config.tierColor(tier.tier());
            tagged = name.copy().withStyle(s -> s.withColor(nameColor));
        }
        return prefix.append(tagged);
    }
}
