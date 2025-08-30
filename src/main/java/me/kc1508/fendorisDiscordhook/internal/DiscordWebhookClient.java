package me.kc1508.fendorisDiscordhook.internal;

import org.bukkit.plugin.java.JavaPlugin;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;

public final class DiscordWebhookClient {
    private final JavaPlugin plugin;
    private final ConfigService cfg;
    private final HttpClient http;
    private final AvatarResolver avatars;

    public DiscordWebhookClient(JavaPlugin plugin, ConfigService cfg, AvatarResolver avatars) {
        this.plugin = plugin;
        this.cfg = cfg;
        this.avatars = avatars;
        this.http = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(cfg.timeoutMs()))
                .build();
    }

    public boolean isConfigured() {
        final var url = cfg.webhookUrl();
        return url.startsWith("http://") || url.startsWith("https://");
    }

    // Chat relay
    public void sendChat(UUID uuid, String playerName, String plainMessage) {
        if (!isConfigured() || !cfg.isEnabled()) return;

        final String content = cfg.chatContentTemplate()
                .replace("<player>", playerName)
                .replace("<message>", plainMessage);

        final String username = cfg.overrideUsername()
                ? truncate(cfg.chatUsernamePrefix() + playerName, 80)
                : null;

        avatars.resolve(uuid, playerName).thenAccept(avatarUrl ->
                post(content, username, avatarUrl)
        );
    }

    // Server event relay (join/quit)
    public void sendServerEvent(UUID uuid, String playerName, String contentTemplate) {
        if (!isConfigured() || !cfg.isEnabled()) return;

        final String content = contentTemplate.replace("<player>", playerName);
        final String username = cfg.overrideUsername()
                ? truncate(cfg.serverUsernamePrefix() + playerName, 80)
                : null;

        avatars.resolve(uuid, playerName).thenAccept(avatarUrl ->
                post(content, username, avatarUrl)
        );
    }

    private void post(String content, String usernameOrNull, String avatarUrlOrNull) {
        final String url = cfg.webhookUrl();

        final var body = new StringBuilder(256);
        body.append('{');
        body.append("\"content\":\"").append(escapeJson(truncate(content, 2000))).append('"');

        if (usernameOrNull != null) {
            body.append(",\"username\":\"").append(escapeJson(usernameOrNull)).append('"');
        }
        if (avatarUrlOrNull != null && !avatarUrlOrNull.isBlank()) {
            body.append(",\"avatar_url\":\"").append(escapeJson(avatarUrlOrNull)).append('"');
        }
        body.append('}');

        // DEBUG LINE (requested)
        if (cfg.debug()) {
            plugin.getLogger().info("[fendoris-discordhook] payload username=" + usernameOrNull + " avatar=" + avatarUrlOrNull);
        }

        final var req = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofMillis(cfg.timeoutMs()))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString(), StandardCharsets.UTF_8))
                .build();

        http.sendAsync(req, HttpResponse.BodyHandlers.discarding())
                .exceptionally(ex -> {
                    plugin.getLogger().warning("Discord webhook send failed: " + ex.getMessage());
                    return null;
                });
    }

    private static String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max);
    }

    private static String escapeJson(String s) {
        final StringBuilder out = new StringBuilder((int) (s.length() * 1.1));
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '\\':
                    out.append("\\\\");
                    break;
                case '"':
                    out.append("\\\"");
                    break;
                case '\b':
                    out.append("\\b");
                    break;
                case '\f':
                    out.append("\\f");
                    break;
                case '\n':
                    out.append("\\n");
                    break;
                case '\r':
                    out.append("\\r");
                    break;
                case '\t':
                    out.append("\\t");
                    break;
                default:
                    if (c < 0x20) out.append(String.format("\\u%04x", (int) c));
                    else out.append(c);
            }
        }
        return out.toString();
    }
}
