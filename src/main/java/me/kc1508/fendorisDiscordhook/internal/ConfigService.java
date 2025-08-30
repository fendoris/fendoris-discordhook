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

    // helpers
    private static String norm(String s) {
        return s == null ? "" : s.trim();
    } // trim

    private static String raw(String s) {
        return s == null ? "" : s;
    }        // preserve spaces

    // toggles
    public boolean isEnabled() {
        return cfg().getBoolean("webhook.enabled", true);
    }

    public boolean serverEventsEnabled() {
        return cfg().getBoolean("server-events.enabled", true);
    }

    // webhook
    public String webhookUrl() {
        return norm(cfg().getString("webhook.url", ""));
    }

    public int timeoutMs() {
        return Math.max(1000, cfg().getInt("webhook.timeout-ms", 5000));
    }

    public boolean overrideUsername() {
        return cfg().getBoolean("webhook.override-username", true);
    }

    // preserve trailing spaces in prefixes
    public String chatUsernamePrefix() {
        return raw(cfg().getString("webhook.username-prefix", ""));
    }

    public String serverUsernamePrefix() {
        return raw(cfg().getString("server-events.username-prefix", "(Server) "));
    }

    public String avatarTemplate() {
        return norm(cfg().getString("webhook.avatar-url-template", ""));
    }

    public String avatarFallbackUrl() {
        return norm(cfg().getString("webhook.avatar-fallback-url", ""));
    }

    public boolean checkAvatarBeforeUse() {
        return cfg().getBoolean("webhook.check-avatar", true);
    }

    // content (do not trim; allow user formatting)
    public String chatContentTemplate() {
        return raw(cfg().getString("webhook.content-template", "<message>"));
    }

    // server events
    public boolean joinEnabled() {
        return cfg().getBoolean("server-events.join.enabled", true);
    }

    public boolean quitEnabled() {
        return cfg().getBoolean("server-events.quit.enabled", true);
    }

    public String joinContentTemplate() {
        return raw(cfg().getString("server-events.join.content-template", "<player> joined"));
    }

    public String quitContentTemplate() {
        return raw(cfg().getString("server-events.quit.content-template", "<player> left"));
    }

    public boolean debug() {
        return cfg().getBoolean("webhook.debug", false);
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
}
