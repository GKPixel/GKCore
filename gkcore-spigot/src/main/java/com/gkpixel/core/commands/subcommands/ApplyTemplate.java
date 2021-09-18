package com.gkpixel.core.commands.subcommands;

import com.gkpixel.core.GKCore;
import com.gkpixel.core.managers.cloudnet.CloudNetFileManager;
import com.gkpixel.core.modules.Commands.CommandManager;
import com.gkpixel.core.modules.Commands.subcommands.Base;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class ApplyTemplate extends Base {
    public ApplyTemplate(JavaPlugin plugin, CommandManager cmd, String name, boolean hidden) {
        super(plugin, cmd, name, hidden);
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (args.length > 1) {
            String templateName = args[1];
            CloudNetFileManager.applyTemplate(templateName);
        }
    }

    @Override
    public String getDescription(CommandSender sender) {
        return GKCore.instance.messageSystem.get(sender, "commands.applyTemplate");
    }

    @Override
    public boolean canExecute(CommandSender sender, String[] args) {
        return sender.hasPermission("gkcore.command.applyTemplate");
    }
}
