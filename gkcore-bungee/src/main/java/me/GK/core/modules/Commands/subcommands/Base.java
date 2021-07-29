package me.GK.core.modules.Commands.subcommands;

import com.google.common.collect.ImmutableList;
import me.GK.core.modules.Commands.CommandManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.Map;

public abstract class Base {
    protected final Plugin plugin;
    protected final CommandManager cmd;

    private final String name;
    private final boolean hidden;

    protected Base(Plugin plugin, CommandManager cmd, String name, boolean hidden) {
        this.plugin = plugin;
        this.cmd = cmd;

        this.name = name;
        this.hidden = hidden;
    }

    public final String getName() {
        return name;
    }

    public final boolean isHidden() {
        return hidden;
    }

    public void recordUsage(Map<Base, Integer> commandUsage) {
        commandUsage.merge(this, 1, Integer::sum);
    }

    public boolean canExecute(CommandSender sender, String[] args) {
        return true;
    }

    public abstract void onExecute(CommandSender sender, String[] args);

    public abstract String getDescription(CommandSender sender);

    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        return ImmutableList.of();
    }
}
