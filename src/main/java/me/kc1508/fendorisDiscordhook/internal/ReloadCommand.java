package me.kc1508.fendorisDiscordhook.internal;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public final class ReloadCommand implements CommandExecutor {
    private final Plugin plugin;
    private final ConfigService cfg;
    private final MessageService msg;
    private final ReloadHook hook;

    @FunctionalInterface
    public interface ReloadHook {
        boolean runAfterReload();
    }

    public ReloadCommand(Plugin plugin, ConfigService cfg, MessageService msg, ReloadHook hook) {
        this.plugin = plugin;
        this.cfg = cfg;
        this.msg = msg;
        this.hook = hook;
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        if (!sender.hasPermission("fendoris.discordhook.reload")) {
            msg.send(sender, cfg.keyNoPerm());
            return true;
        }
        final boolean ready = hook.runAfterReload();
        if (!ready) {
            msg.send(sender, cfg.keyWebhookMissing());
            plugin.getLogger().warning("Webhook missing/disabled after reload.");
            return true;
        }
        msg.send(sender, cfg.keyReloadOk());
        return true;
    }
}
