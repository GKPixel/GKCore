package com.gkpixel.core.commands.subcommands;

import com.gkpixel.core.GKCore;
import com.gkpixel.core.containers.InputListener;
import com.gkpixel.core.main.Extensions;
import com.gkpixel.core.modules.Commands.CommandManager;
import com.gkpixel.core.modules.Commands.subcommands.Base;
import com.gkpixel.core.modules.GKPlayerDatabase;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class RunCommandWithInput extends Base {
    public RunCommandWithInput(JavaPlugin plugin, CommandManager cmd, String name, boolean hidden) {
        super(plugin, cmd, name, hidden);
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (args.length > 2) {
            String targetName = args[1];
            Player target = Bukkit.getPlayer(targetName);
            if (target == null) return;
            String command = "";
            for (int i = 2; i < args.length; i++) {
                command += args[i] + " ";
            }
            String finalCommand = command;
            sender.sendMessage("final command: " + finalCommand);
            InputListener.create(target.getUniqueId(), new Runnable() {
                @Override
                public void run() {
                    String result = InputListener.getInput(target);
                    String realCmd = finalCommand;
                    realCmd = realCmd.replaceAll("%input%", result);
                    realCmd = realCmd.replaceAll("\\{input\\}", result);
                    realCmd = realCmd.replaceAll("%player%", target.getName());
                    realCmd = realCmd.replaceAll("\\{player\\}", target.getName());
                    Extensions.runServerCommand(realCmd);
                }
            }).send();
        }
    }

    @Override
    public String getDescription(CommandSender sender) {
        return GKCore.instance.messageSystem.get(sender, "commands.languageDescription");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return CommandManager.createReturnList(GKCore.instance.configSystem.config.getStringList("languages"), args[1], false);
    }
}
