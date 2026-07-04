package com.meekdev.frkdtagger.render;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public final class PlayerLookup {
    private PlayerLookup() {
    }

    public static Player lookedAt(Minecraft mc, double maxDistance) {
        Entity camera = mc.getCameraEntity();
        if (camera == null || mc.level == null) return null;

        Vec3 eye = camera.getEyePosition();
        Vec3 look = camera.getViewVector(1.0F);
        Vec3 end = eye.add(look.scale(maxDistance));

        double closest = maxDistance * maxDistance;
        Player best = null;

        for (Player player : mc.level.players()) {
            if (player == mc.player) continue;
            AABB box = player.getBoundingBox().inflate(0.35);
            Optional<Vec3> hit = box.clip(eye, end);
            if (box.contains(eye) || hit.isPresent()) {
                double distance = eye.distanceToSqr(hit.orElse(player.position()));
                if (distance < closest) {
                    closest = distance;
                    best = player;
                }
            }
        }
        return best;
    }
}
