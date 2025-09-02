package me.kc1508.fendorisDiscordhook.internal;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class ServerEventsListener implements Listener {
    private final DiscordWebhookClient client;
    private final ConfigService cfg;
    private static final PlainTextComponentSerializer PLAIN = PlainTextComponentSerializer.plainText();

    public ServerEventsListener(DiscordWebhookClient client, ConfigService cfg) {
        this.client = client;
        this.cfg = cfg;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent e) {
        if (!cfg.joinEnabled()) return;
        client.sendServerEvent(e.getPlayer().getName(), cfg.joinContentTemplate());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent e) {
        if (!cfg.quitEnabled()) return;
        client.sendServerEvent(e.getPlayer().getName(), cfg.quitContentTemplate());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent e) {
        if (!cfg.deathEnabled()) return;
        final Component comp = e.deathMessage();
        final String msg = comp != null ? PLAIN.serialize(comp) : e.getPlayer().getName() + " died";
        client.sendServerText(msg);
    }
}
