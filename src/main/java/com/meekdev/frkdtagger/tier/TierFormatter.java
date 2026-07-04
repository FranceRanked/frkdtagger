package com.meekdev.frkdtagger.tier;

import com.meekdev.frkdtagger.FrkdTagger;
import com.meekdev.frkdtagger.api.PlayerTier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final class TierFormatter {
    private static final char G_HIGH = '\uE710';
    private static final char G_LOW = '\uE711';
    private static final char G_T = '\uE712';
    private static final char[] G_DIGIT = {0, '\uE713', '\uE714', '\uE715', '\uE716', '\uE717'};

    private TierFormatter() {
    }

    public static int color(PlayerTier tier) {
        boolean retired = tier.isRetired() && tier.hasPeak();
        int value = retired ? tier.peakTier() : tier.tier();
        return retired ? FrkdTagger.config().retiredColor() : FrkdTagger.config().tierColor(value);
    }

    private static boolean hasGlyphs(int value) {
        return value >= 1 && value < G_DIGIT.length && G_DIGIT[value] != 0;
    }

    private static String glyphs(int value, boolean high) {
        return "" + (high ? G_HIGH : G_LOW) + G_T + G_DIGIT[value];
    }

    public static String glyphText(PlayerTier tier) {
        boolean retired = tier.isRetired() && tier.hasPeak();
        int value = retired ? tier.peakTier() : tier.tier();
        boolean high = retired ? tier.peakIsHigh() : tier.isHigh();
        return hasGlyphs(value) ? glyphs(value, high) : (retired ? "R" : "") + (high ? "H" : "L") + "T" + value;
    }

    public static Component label(PlayerTier tier) {
        boolean retired = tier.isRetired() && tier.hasPeak();
        int value = retired ? tier.peakTier() : tier.tier();
        boolean high = retired ? tier.peakIsHigh() : tier.isHigh();
        int color = retired
                ? FrkdTagger.config().retiredColor()
                : FrkdTagger.config().tierColor(value);

        String text = hasGlyphs(value) ? glyphs(value, high) : (retired ? "R" : "") + (high ? "H" : "L") + "T" + value;
        MutableComponent label = Component.literal(text).withStyle(s -> s.withColor(color));

        if (!retired && tier.hasPeak() && tier.peakRank() < tier.rank()) {
            int peakValue = tier.peakTier();
            boolean peakHigh = tier.peakIsHigh();
            String peak = hasGlyphs(peakValue) ? glyphs(peakValue, peakHigh) : (peakHigh ? "H" : "L") + "T" + peakValue;
            label.append(Component.literal(" (").withStyle(ChatFormatting.GRAY))
                    .append(Component.literal(peak).withStyle(s -> s.withColor(FrkdTagger.config().tierColor(peakValue))))
                    .append(Component.literal(")").withStyle(ChatFormatting.GRAY));
        }

        return label;
    }
}
