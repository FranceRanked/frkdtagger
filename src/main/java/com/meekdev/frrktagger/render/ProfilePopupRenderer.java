package com.meekdev.frrktagger.render;

import com.meekdev.frrktagger.FrrkTagger;
import com.meekdev.frrktagger.input.Keybinds;
import com.meekdev.frrktagger.tier.TierCache;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
//? if >=26 {
/*import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
*///?} else {
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
//?}
import net.minecraft.client.Minecraft;
//? if !v262 {
import net.minecraft.client.renderer.MultiBufferSource;
//?}
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public final class ProfilePopupRenderer {
    private static final double MAX_DISTANCE = 64.0;
    private static final int FULL_BRIGHT = 0xF000F0;
    private static final float FADE_SECONDS = 0.16F;

    private static float anim;
    private static long lastMs;
    private static Player heldTarget;
    private static CardTextures.Card heldCard;

    private ProfilePopupRenderer() {
    }

    public static void renderWorld(/*? if >=26 {*//*LevelRenderContext*//*?} else {*/ WorldRenderContext /*?}*/ ctx) {
        Minecraft mc = Minecraft.getInstance();

        boolean want = FrrkTagger.config().showPopup()
                && Keybinds.popup != null && Keybinds.popup.isDown()
                && mc.player != null && mc.level != null;

        CardTextures.Card card = null;
        if (want) {
            Player target = PlayerLookup.lookedAt(mc, MAX_DISTANCE);
            if (target != null && TierCache.profile(target.getUUID()).isPresent()) {
                card = CardTextures.get(target.getUUID()).orElse(null);
                if (card != null) {
                    heldTarget = target;
                    heldCard = card;
                }
            }
        }
        boolean showing = card != null;

        long now = System.currentTimeMillis();
        float dt = lastMs == 0 ? 0.0F : Math.min(0.1F, (now - lastMs) / 1000.0F);
        lastMs = now;
        float goal = showing ? 1.0F : 0.0F;
        float step = dt / FADE_SECONDS;
        anim = anim < goal ? Math.min(goal, anim + step) : Math.max(goal, anim - step);

        if (anim <= 0.001F || heldTarget == null || heldCard == null || heldTarget.isRemoved()) {
            if (anim <= 0.001F) {
                anim = 0.0F;
                heldTarget = null;
                heldCard = null;
            }
            return;
        }

        Player t = heldTarget;
        CardTextures.Card c = heldCard;
        float ease = anim * anim * (3.0F - 2.0F * anim);
        int alpha = Math.round(ease * 255.0F);

        float width = FrrkTagger.config().popupWidth() * (0.85F + 0.15F * ease);
        float height = width * (c.height() / (float) c.width());
        float hw = width / 2.0F;
        float hh = height / 2.0F;

        Vec3 cam = mc.gameRenderer./*? if v262 {*//*mainCamera*//*?} else {*/ getMainCamera /*?}*/().position();
        double ax = t.getX();
        double ay = t.getY() + t.getBbHeight() * 0.6 + FrrkTagger.config().popupOffset();
        double az = t.getZ();

        PoseStack pose = ctx./*? if >=26 {*//*poseStack()*//*?} else {*/ matrices() /*?}*/;
        pose.pushPose();
        pose.translate(ax - cam.x, ay - cam.y, az - cam.z);
        pose.mulPose(mc.gameRenderer./*? if v262 {*//*mainCamera*//*?} else {*/ getMainCamera /*?}*/().rotation());
        pose.translate(hw + 0.5F, 0.0F, 0.0F);

                //? if !v262 {
        Matrix4f matrix = pose.last().pose();
        RenderType type = RenderTypes.entityTranslucent(c.id());
        MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();
        VertexConsumer vc = buffers.getBuffer(type);

        quad(vc, matrix, hw, hh, 1.0F, alpha);
        quad(vc, matrix, hw, hh, -1.0F, alpha);

        pose.popPose();
        buffers.endBatch(type);
        //?} else {
        /*ctx.submitNodeCollector().submitCustomGeometry(pose, RenderTypes.entityTranslucent(c.id()), (view, vc) -> {
            Matrix4f matrix = view.pose();
            quad(vc, matrix, hw, hh, 1.0F, alpha);
            quad(vc, matrix, hw, hh, -1.0F, alpha);
        });
        pose.popPose();
        *///?}
    }

    private static void quad(VertexConsumer vc, Matrix4f m, float hw, float hh, float facing, int alpha) {
        if (facing > 0) {
            vertex(vc, m, -hw, hh, 0, 0, facing, alpha);
            vertex(vc, m, -hw, -hh, 0, 1, facing, alpha);
            vertex(vc, m, hw, -hh, 1, 1, facing, alpha);
            vertex(vc, m, hw, hh, 1, 0, facing, alpha);
        } else {
            vertex(vc, m, hw, hh, 1, 0, facing, alpha);
            vertex(vc, m, hw, -hh, 1, 1, facing, alpha);
            vertex(vc, m, -hw, -hh, 0, 1, facing, alpha);
            vertex(vc, m, -hw, hh, 0, 0, facing, alpha);
        }
    }

    private static void vertex(VertexConsumer vc, Matrix4f m, float x, float y, float u, float v, float nz, int alpha) {
        vc.addVertex(m, x, y, 0.0F)
                .setColor(255, 255, 255, alpha)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(FULL_BRIGHT)
                .setNormal(0.0F, 0.0F, nz);
    }
}
