package com.meekdev.frkdtagger.mixin;

import com.meekdev.frkdtagger.FrkdTagger;
import com.meekdev.frkdtagger.hud.TierDisplay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.DisplayRenderer;
import net.minecraft.client.renderer.entity.state.TextDisplayEntityRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(DisplayRenderer.TextDisplayRenderer.class)
public class TextDisplayMixin {
    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/Display$TextDisplay;Lnet/minecraft/client/renderer/entity/state/TextDisplayEntityRenderState;F)V", at = @At("RETURN"))
    private void frkdtagger$addTier(Display.TextDisplay entity, TextDisplayEntityRenderState renderState, float partialTick, CallbackInfo ci) {
        if (!FrkdTagger.config().enabled() || !FrkdTagger.config().showInNametag()) return;
        if (renderState.cachedInfo == null) return;
        if (!(entity.getVehicle() instanceof Player player)) return;

        String name = player.getScoreboardName();
        List<Display.TextDisplay.CachedLine> lines = renderState.cachedInfo.lines();
        for (int i = 0; i < lines.size(); i++) {
            Component styled = toComponent(lines.get(i).contents());
            if (!styled.getString().contains(name)) continue;

            Component modified = TierDisplay.append(player.getUUID(), styled);
            if (modified == styled) return;

            FormattedCharSequence sequence = modified.getVisualOrderText();
            int lineWidth = Minecraft.getInstance().font.width(modified);

            List<Display.TextDisplay.CachedLine> newLines = new ArrayList<>(lines);
            newLines.set(i, new Display.TextDisplay.CachedLine(sequence, lineWidth));
            int maxWidth = newLines.stream().mapToInt(Display.TextDisplay.CachedLine::width)
                    .max().orElse(renderState.cachedInfo.width());
            renderState.cachedInfo = new Display.TextDisplay.CachedInfo(newLines, maxWidth);
            return;
        }
    }

    private static Component toComponent(FormattedCharSequence sequence) {
        MutableComponent result = Component.empty();
        StringBuilder run = new StringBuilder();
        Style[] current = {Style.EMPTY};
        sequence.accept((index, style, codePoint) -> {
            if (!style.equals(current[0])) {
                if (!run.isEmpty()) {
                    Style flushed = current[0];
                    result.append(Component.literal(run.toString()).withStyle(flushed));
                    run.setLength(0);
                }
                current[0] = style;
            }
            run.appendCodePoint(codePoint);
            return true;
        });
        if (!run.isEmpty()) {
            result.append(Component.literal(run.toString()).withStyle(current[0]));
        }
        return result;
    }
}
