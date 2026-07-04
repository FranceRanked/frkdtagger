package com.meekdev.frkdtagger.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.meekdev.frkdtagger.FrkdTagger;
import com.meekdev.frkdtagger.hud.TierDisplay;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.UUID;

@Mixin(value = PlayerTabOverlay.class, priority = 1500)
public class PlayerTabOverlayMixin {
    @ModifyReturnValue(method = "getNameForDisplay", at = @At("RETURN"))
    private Component frkdtagger$appendTier(Component original, PlayerInfo entry) {
        if (!FrkdTagger.config().showInPlayerList()) return original;

        UUID uuid =
                //? if >=1.21.11 {
                entry.getProfile().id();
                //?} else {
                /*entry.getProfile().getId();
                *///?}

        Component base = entry.getTabListDisplayName();
        if (base == null) {
            base = Component.literal(
                    //? if >=1.21.11 {
                    entry.getProfile().name()
                    //?} else {
                    /*entry.getProfile().getName()
                    *///?}
            );
        }
        return TierDisplay.append(uuid, base);
    }
}
