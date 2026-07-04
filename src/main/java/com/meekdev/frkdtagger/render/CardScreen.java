package com.meekdev.frkdtagger.render;

//? if >=26 {
/*import net.minecraft.client.gui.GuiGraphicsExtractor;
*///?} else {
import net.minecraft.client.gui.GuiGraphics;
//?}
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;

import java.util.Optional;
import java.util.UUID;

public class CardScreen extends Screen {
    private final UUID uuid;

    public CardScreen(UUID uuid, String name) {
        super(Component.literal(name));
        this.uuid = uuid;
    }

    @Override
    public void /*? if >=26 {*//*extractRenderState*//*?} else {*/ render /*?}*/(/*? if >=26 {*//*GuiGraphicsExtractor*//*?} else {*/ GuiGraphics /*?}*/ graphics, int mouseX, int mouseY, float partial) {
        super./*? if >=26 {*//*extractRenderState*//*?} else {*/ render /*?}*/(graphics, mouseX, mouseY, partial);

        Optional<CardTextures.Card> opt = CardTextures.get(uuid);
        if (opt.isEmpty()) {
            graphics./*? if >=26 {*//*centeredText*//*?} else {*/ drawCenteredString /*?}*/(this.font,
                    Component.literal("Chargement de la carte…"), this.width / 2, this.height / 2, 0xFFFFFFFF);
            return;
        }

        CardTextures.Card card = opt.get();
        float scale = Math.min(this.width * 0.8F / card.width(), this.height * 0.85F / card.height());
        int dw = Math.round(card.width() * scale);
        int dh = Math.round(card.height() * scale);
        int x = (this.width - dw) / 2;
        int y = (this.height - dh) / 2;

        graphics.blit(RenderPipelines.GUI_TEXTURED, card.id(), x, y, 0.0F, 0.0F,
                dw, dh, card.width(), card.height(), card.width(), card.height());
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
