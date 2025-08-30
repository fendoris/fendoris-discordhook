package me.kc1508.fendorisDiscordhook.internal;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public final class ChatRelayListener implements Listener {
    private final DiscordWebhookClient client;
    private static final PlainTextComponentSerializer PLAIN = PlainTextComponentSerializer.plainText();

    public ChatRelayListener(DiscordWebhookClient client) {
        this.client = client;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAsyncChat(AsyncChatEvent e) {
        final var p = e.getPlayer();
        final String message = PLAIN.serialize(e.message());
        client.sendChat(p.getUniqueId(), p.getName(), message);
    }

    public static void unregisterAll(Plugin plugin) {
        HandlerList.unregisterAll(plugin);
    }
}
