package me.kc1508.fendorisDiscordhook.internal;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class ConfigService {
    private final JavaPlugin plugin;

    public ConfigService(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void reload() {
        plugin.reloadConfig();
    }

    private FileConfiguration cfg() {
        return plugin.getConfig();
    }

    // toggles
    public boolean isEnabled() {
        return cfg().getBoolean("webhook.enabled", true);
    }

    public boolean serverEventsEnabled() {
        return cfg().getBoolean("server-events.enabled", true);
    }

    // webhook
    public String webhookUrl() {
        return safe(cfg().getString("webhook.url", ""));
    }

    public int timeoutMs() {
        return Math.max(1000, cfg().getInt("webhook.timeout-ms", 5000));
    }

    public boolean overrideUsername() {
        return cfg().getBoolean("webhook.override-username", true);
    }

    public String chatUsernamePrefix() {
        return safe(cfg().getString("webhook.username-prefix", ""));
    }

    public String avatarTemplate() {
        return safe(cfg().getString("webhook.avatar-url-template", ""));
    } // tokens: <uuid>, <player>

    public String avatarFallbackUrl() {
        return safe(cfg().getString("webhook.avatar-fallback-url", ""));
    }

    public boolean checkAvatarBeforeUse() {
        return cfg().getBoolean("webhook.check-avatar", true);
    }

    // content
    // tokens: <player>, <message>
    public String chatContentTemplate() {
        return safe(cfg().getString("webhook.content-template", "<message>"));
    }

    // server events
    public String serverUsernamePrefix() {
        return safe(cfg().getString("server-events.username-prefix", "(Server) "));
    }

    public boolean joinEnabled() {
        return cfg().getBoolean("server-events.join.enabled", true);
    }

    public boolean quitEnabled() {
        return cfg().getBoolean("server-events.quit.enabled", true);
    }

    // tokens: <player>
    public String joinContentTemplate() {
        return safe(cfg().getString("server-events.join.content-template", "<player> joined"));
    }

    public String quitContentTemplate() {
        return safe(cfg().getString("server-events.quit.content-template", "<player> left"));
    }

    // lang keys
    public String keyReloadOk() {
        return "messages.reload-success";
    }

    public String keyNoPerm() {
        return "messages.no-permission";
    }

    public String keyWebhookMissing() {
        return "messages.webhook-missing";
    }

    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }
}
