package me.kc1508.fendorisDiscordhook.internal;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class AvatarResolver {
    private final ConfigService cfg;
    private final HttpClient http;

    public AvatarResolver(ConfigService cfg) {
        this.cfg = cfg;
        this.http = HttpClient.newBuilder().connectTimeout(Duration.ofMillis(Math.max(1000, cfg.timeoutMs()))).build();
    }

    public CompletableFuture<String> resolve(UUID uuid, String playerName) {
        final String uuidNoDash = uuid.toString().replace("-", "");
        final String template = cfg.avatarTemplate();
        final String fallback = cfg.avatarFallbackUrl();

        final String primary = template.replace("<uuid_nodash>", uuidNoDash).replace("<uuid>", uuid.toString()).replace("<player>", playerName);

        if (primary.isBlank()) return CompletableFuture.completedFuture(fallback);
        if (!cfg.checkAvatarBeforeUse()) return CompletableFuture.completedFuture(primary);

        final var req = HttpRequest.newBuilder(URI.create(primary)).timeout(Duration.ofMillis(Math.max(1000, cfg.timeoutMs()))).method("HEAD", HttpRequest.BodyPublishers.noBody()).build();

        return http.sendAsync(req, HttpResponse.BodyHandlers.discarding()).thenApply(resp -> (resp.statusCode() >= 200 && resp.statusCode() < 300) ? primary : fallback).exceptionally(ex -> fallback);
    }
}
