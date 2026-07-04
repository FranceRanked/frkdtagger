package com.meekdev.frrktagger.config.gui;

import com.meekdev.frrktagger.FrrkTagger;
import com.meekdev.frrktagger.config.FrrkConfig;
import com.meekdev.frrktagger.tier.TierCache;
import net.minecraft.client.Minecraft;
//? if >=26 {
/*import net.minecraft.client.gui.GuiGraphicsExtractor;
*///?} else {
import net.minecraft.client.gui.GuiGraphics;
//?}
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

public class ConfigScreen extends Screen {
    private final Screen parent;
    private final FrrkConfig config = FrrkTagger.config();
    private int tierRowX;
    private int tierRowY;
    private int tierStep;

    public ConfigScreen(Screen parent) {
        super(Component.translatable("frrktagger.config.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int left = cx - 155;
        int right = cx + 5;
        int w = 150;
        int h = 20;
        int step = 24;
        int y = 36;

        addRenderableWidget(toggle(left, y, w, h, "frrktagger.opt.enabled", config.enabled(), config::setEnabled));
        addRenderableWidget(toggle(right, y, w, h, "frrktagger.opt.nametag", config.showInNametag(), config::setShowInNametag));
        y += step;
        addRenderableWidget(toggle(left, y, w, h, "frrktagger.opt.playerlist", config.showInPlayerList(), config::setShowInPlayerList));
        addRenderableWidget(toggle(right, y, w, h, "frrktagger.opt.kiticon", config.showKitIcon(), config::setShowKitIcon));
        y += step;
        addRenderableWidget(toggle(left, y, w, h, "frrktagger.opt.colorname", config.colorNameByTier(), config::setColorNameByTier));
        addRenderableWidget(toggle(right, y, w, h, "frrktagger.opt.warning", config.showSanctionWarning(), config::setShowSanctionWarning));
        y += step;
        addRenderableWidget(toggle(left, y, w, h, "frrktagger.opt.chat", config.chatTags(), config::setChatTags));
        addRenderableWidget(toggle(right, y, w, h, "frrktagger.opt.popup", config.showPopup(), config::setShowPopup));
        y += step;

        addRenderableWidget(highestButton(left, y, w, h));
        addRenderableWidget(kitButton(right, y, w, h));
        y += step;

        if (canEditEndpoint()) {
            addRenderableWidget(editBox(left, y, w, h, "frrktagger.opt.api", config.apiBaseUrl(), config::setApiBaseUrl));
            addRenderableWidget(editBox(right, y, w, h, "frrktagger.opt.apipath", config.apiPath(), config::setApiPath));
            y += step;

            EditBox ttl = new EditBox(this.font, left, y, w, h, Component.translatable("frrktagger.opt.ttl"));
            ttl.setValue(String.valueOf(config.cacheTtlSeconds()));
            ttl.setResponder(s -> {
                try {
                    config.setCacheTtlSeconds(Integer.parseInt(s.trim()));
                } catch (NumberFormatException ignored) {
                }
            });
            addRenderableWidget(ttl);
            y += step + 12;
        } else {
            y += 12;
        }

        tierRowX = left;
        tierRowY = y;
        tierStep = 52;
        for (int i = 0; i < 5; i++) {
            int tier = i + 1;
            addRenderableWidget(colorBox(tierRowX + i * tierStep, y, 47,
                    () -> config.tierColor(tier), v -> config.setTierColor(tier, v)));
        }
        addRenderableWidget(colorBox(tierRowX + 5 * tierStep, y, 47,
                config::retiredColor, config::setRetiredColor));
        y += step + 10;

        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, b -> onClose())
                .bounds(cx - 75, y, 150, 20).build());
    }

    private EditBox colorBox(int x, int y, int w, IntSupplier getter,
                             IntConsumer setter) {
        EditBox box = new EditBox(this.font, x, y, w, 20, Component.literal("hex"));
        box.setMaxLength(7);
        box.setValue(String.format("%06X", getter.getAsInt() & 0xFFFFFF));
        box.setResponder(s -> {
            try {
                setter.accept(Integer.parseInt(s.replace("#", "").trim(), 16) & 0xFFFFFF);
            } catch (NumberFormatException ignored) {
            }
        });
        return box;
    }

    private static boolean canEditEndpoint() {
        return "Meekanographie".equals(Minecraft.getInstance().getUser().getName());
    }

    private CycleButton<Boolean> toggle(int x, int y, int w, int h, String key, boolean value, Consumer<Boolean> setter) {
        return CycleButton.onOffBuilder(value)
                .create(x, y, w, h, Component.translatable(key), (button, v) -> setter.accept(v));
    }

    private EditBox editBox(int x, int y, int w, int h, String key, String value, Consumer<String> setter) {
        EditBox box = new EditBox(this.font, x, y, w, h, Component.translatable(key));
        box.setMaxLength(256);
        box.setValue(value);
        box.setResponder(setter);
        return box;
    }

    private Button highestButton(int x, int y, int w, int h) {
        return Button.builder(highestLabel(), button -> {
            FrrkConfig.HighestMode[] values = FrrkConfig.HighestMode.values();
            FrrkConfig.HighestMode next = values[(config.highestMode().ordinal() + 1) % values.length];
            config.setHighestMode(next);
            button.setMessage(highestLabel());
        }).bounds(x, y, w, h).build();
    }

    private Component highestLabel() {
        return Component.translatable("frrktagger.opt.highest")
                .append(": ").append(Component.literal(config.highestMode().name()));
    }

    private Button kitButton(int x, int y, int w, int h) {
        return Button.builder(kitLabel(), button -> {
            config.setGameMode(TierCache.nextMode(config.gameMode()));
            button.setMessage(kitLabel());
        }).bounds(x, y, w, h).build();
    }

    private Component kitLabel() {
        List<String> modes = TierCache.modes().stream().map(m -> m.slug()).toList();
        String value = modes.isEmpty() ? config.gameMode() : config.gameMode();
        return Component.translatable("frrktagger.opt.kit").append(": ").append(Component.literal(value));
    }

    @Override
    public void /*? if >=26 {*//*extractRenderState*//*?} else {*/ render /*?}*/(/*? if >=26 {*//*GuiGraphicsExtractor*//*?} else {*/ GuiGraphics /*?}*/ graphics, int mouseX, int mouseY, float partial) {
        super./*? if >=26 {*//*extractRenderState*//*?} else {*/ render /*?}*/(graphics, mouseX, mouseY, partial);
        graphics./*? if >=26 {*//*centeredText*//*?} else {*/ drawCenteredString /*?}*/(this.font, this.title, this.width / 2, 16, 0xFFFFFFFF);
        String[] names = {"T1", "T2", "T3", "T4", "T5", "R"};
        for (int i = 0; i < names.length; i++) {
            int color = i < 5 ? config.tierColor(i + 1) : config.retiredColor();
            graphics./*? if >=26 {*//*text*//*?} else {*/ drawString /*?}*/(this.font, names[i], tierRowX + i * tierStep + 1, tierRowY - 10, 0xFF000000 | color);
        }
    }

    @Override
    public void onClose() {
        config.save();
        if (this.minecraft != null) this.minecraft./*? if v262 {*//*gui.setScreen*//*?} else {*/ setScreen /*?}*/(parent);
    }
}
