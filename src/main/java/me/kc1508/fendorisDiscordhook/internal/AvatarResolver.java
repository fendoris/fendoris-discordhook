package me.kc1508.fendorisDiscordhook.internal;

import java.net.URI;
import java.net.URISyntaxException;
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
        this.http = HttpClient.newBuilder().connectTimeout(Duration.ofMillis(Math.max(1000, cfg.timeoutMs()))).followRedirects(HttpClient.Redirect.ALWAYS).build();
    }

    public CompletableFuture<String> resolve(UUID uuid, String playerName) {
        final String fallback = cfg.avatarFallbackUrl();
        final String template = cfg.avatarTemplate();

        if (template == null || template.isBlank()) {
            dbg("template blank -> fallback");
            return CompletableFuture.completedFuture(fallback);
        }
        final boolean hasToken = template.contains("<uuid") || template.contains("<player>");
        if (!hasToken) {
            dbg("template has no tokens -> fallback");
            return CompletableFuture.completedFuture(fallback);
        }

        final String uuidNoDash = uuid.toString().replace("-", "");
        final String primaryUrl = addStableCacheKey(template.replace("<uuid_nodash>", uuidNoDash).replace("<uuid>", uuid.toString()).replace("<player>", playerName), uuidNoDash);

        if (!isValidHttpUrl(primaryUrl)) {
            dbg("invalid url: " + primaryUrl + " -> fallback");
            return CompletableFuture.completedFuture(fallback);
        }

        if (!cfg.checkAvatarBeforeUse()) {
            dbg("check disabled -> " + primaryUrl);
            return CompletableFuture.completedFuture(primaryUrl);
        }

        // Use GET instead of HEAD. Some CDNs reject HEAD.
        final HttpRequest req = HttpRequest.newBuilder(URI.create(primaryUrl)).timeout(Duration.ofMillis(Math.max(1000, cfg.timeoutMs()))).GET().build();

        dbg("probe GET " + primaryUrl);
        return http.sendAsync(req, HttpResponse.BodyHandlers.discarding()).thenApply(resp -> {
            final int code = resp.statusCode();
            dbg("probe status " + code + " for " + primaryUrl);
            return (code >= 200 && code < 300) ? primaryUrl : fallback;
        }).exceptionally(ex -> {
            dbg("probe error " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
            return fallback;
        });
    }

    private static String addStableCacheKey(String url, String key) {
        if (url.contains("cb=" + key)) return url;
        return url + (url.contains("?") ? "&" : "?") + "cb=" + key;
    }

    private static boolean isValidHttpUrl(String url) {
        try {
            final var u = new URI(url);
            final String s = u.getScheme();
            return "http".equalsIgnoreCase(s) || "https".equalsIgnoreCase(s);
        } catch (URISyntaxException | IllegalArgumentException e) {
            return false;
        }
    }

    private void dbg(String m) {
        if (cfg.debug()) {
            // Avoid spam. Short, single-line entries.
            System.out.println("[fendoris-discordhook][avatar] " + m);
        }
    }
}
