package com.meekdev.frkdtagger.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.meekdev.frkdtagger.FrkdTagger;
import com.meekdev.frkdtagger.hud.TierDisplay;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = Player.class, priority = 1500)
public class PlayerMixin {
    @ModifyReturnValue(method = "getDisplayName", at = @At("RETURN"))
    private Component frkdtagger$appendTier(Component original) {
        if (!FrkdTagger.config().showInNametag()) return original;
        Player self = (Player) (Object) this;
        return TierDisplay.append(self.getUUID(), self.getName());
    }
}
