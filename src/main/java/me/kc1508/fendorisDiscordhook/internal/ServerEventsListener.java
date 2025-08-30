package me.kc1508.fendorisDiscordhook.internal;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class ServerEventsListener implements Listener {
    private final DiscordWebhookClient client;
    private final ConfigService cfg;

    public ServerEventsListener(DiscordWebhookClient client, ConfigService cfg) {
        this.client = client;
        this.cfg = cfg;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent e) {
        if (!cfg.joinEnabled()) return;
        final var p = e.getPlayer();
        client.sendServerEvent(p.getUniqueId(), p.getName(), cfg.joinContentTemplate());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent e) {
        if (!cfg.quitEnabled()) return;
        final var p = e.getPlayer();
        client.sendServerEvent(p.getUniqueId(), p.getName(), cfg.quitContentTemplate());
    }
}
