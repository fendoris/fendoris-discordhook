package me.kc1508.fendorisDiscordhook;

import me.kc1508.fendorisDiscordhook.internal.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class FendorisDiscordhook extends JavaPlugin {
    private ConfigService configService;
    private AvatarResolver avatarResolver;
    private DiscordWebhookClient webhookClient;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.configService = new ConfigService(this);
        MessageService messageService = new MessageService(this);
        this.avatarResolver = new AvatarResolver(configService);
        this.webhookClient = new DiscordWebhookClient(this, configService, avatarResolver);

        wireListeners();

        Objects.requireNonNull(getCommand("discordhookreload")).setExecutor(new ReloadCommand(this, configService, messageService, () -> {
            configService.reload();
            this.avatarResolver = new AvatarResolver(configService);
            this.webhookClient = new DiscordWebhookClient(this, configService, avatarResolver);
            ChatRelayListener.unregisterAll(this);
            wireListeners();
            return configService.isEnabled() && webhookClient.isConfigured();
        }));
    }

    private void wireListeners() {
        if (configService.isEnabled() && webhookClient.isConfigured()) {
            getServer().getPluginManager().registerEvents(new ChatRelayListener(webhookClient), this);
            if (configService.serverEventsEnabled()) {
                getServer().getPluginManager().registerEvents(new ServerEventsListener(webhookClient, configService), this);
            }
        } else {
            getLogger().warning("Discord relay disabled or webhook not configured.");
        }
    }

    @Override
    public void onDisable() {
    }
}
