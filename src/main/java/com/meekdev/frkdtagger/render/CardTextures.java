package com.meekdev.frkdtagger.render;

import com.meekdev.frkdtagger.FrkdTagger;
import com.meekdev.frkdtagger.api.FranceRankedApi;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class CardTextures {
    public record Card(Identifier id, int width, int height) {
    }

    private static final Map<UUID, Card> READY = new ConcurrentHashMap<>();
    private static final Map<UUID, Long> PENDING = new ConcurrentHashMap<>();
    private static final long RETRY_MS = 20_000;

    private CardTextures() {
    }

    public static Optional<Card> get(UUID uuid) {
        Card card = READY.get(uuid);
        if (card != null) return Optional.of(card);
        request(uuid);
        return Optional.empty();
    }

    public static void invalidate(UUID uuid) {
        READY.remove(uuid);
        PENDING.remove(uuid);
    }

    private static void request(UUID uuid) {
        long now = System.currentTimeMillis();
        Long last = PENDING.get(uuid);
        if (last != null && now - last < RETRY_MS) return;
        PENDING.put(uuid, now);

        FranceRankedApi.cardBytes(uuid).whenComplete((bytes, error) -> {
            if (bytes == null || bytes.length == 0) return;
            Minecraft.getInstance().execute(() -> upload(uuid, bytes));
        });
    }

    private static void upload(UUID uuid, byte[] bytes) {
        try {
            NativeImage image = NativeImage.read(bytes);
            DynamicTexture texture = new DynamicTexture(() -> "frkdtagger-card-" + uuid, image);
            Identifier id = Identifier.fromNamespaceAndPath("frkdtagger", "card/" + uuid);
            Minecraft.getInstance().getTextureManager().register(id, texture);
            READY.put(uuid, new Card(id, image.getWidth(), image.getHeight()));
            PENDING.remove(uuid);
        } catch (Exception e) {
        }
    }
}
