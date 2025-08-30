package me.kc1508.fendorisDiscordhook.internal;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class MessageService {
    private final JavaPlugin plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();
    private static final String FB = "Error in language string.";

    public MessageService(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void send(CommandSender to, String path, TagResolver... tags) {
        final FileConfiguration cfg = plugin.getConfig();
        final String raw = cfg.getString(path, FB);
        to.sendMessage(mm.deserialize(raw, tags));
    }

}
