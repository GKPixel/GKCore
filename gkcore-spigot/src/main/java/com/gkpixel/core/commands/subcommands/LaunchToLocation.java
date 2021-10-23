package com.gkpixel.core.commands.subcommands;

import com.gkpixel.core.GKCore;
import com.gkpixel.core.modules.Commands.CommandManager;
import com.gkpixel.core.modules.Commands.subcommands.Base;
import com.gkpixel.core.utils.GKPhysics;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.ext.bridge.player.IPlayerManager;
import de.dytanic.cloudnet.ext.bridge.player.executor.ServerSelectorType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class LaunchToLocation extends Base {
    public LaunchToLocation(JavaPlugin plugin, CommandManager cmd, String name, boolean hidden) {
        super(plugin, cmd, name, hidden);
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if(args.length < 3){
            sender.sendMessage("Should be at least 3 arguments");
            return;
        }
        String playerName= args[1];
        Player player = Bukkit.getPlayer(playerName);
        String locString = args[2];
        Vector targetVector = GKPhysics.LocationVectorFromString(locString);
        String levelString = args[3];

        float targetOffsetY = 0;
        if (args.length > 4) {
            targetOffsetY = Float.parseFloat(args[4]);
        }
        Location targetLoc = new Location(player.getWorld(), targetVector.getX(),
                targetVector.getY() + targetOffsetY, targetVector.getZ());

        LivingEntity p1 = (LivingEntity) sender;
        double level = -Double.parseDouble(levelString);
        Vector dir = GKPhysics.vector3_difference(p1.getLocation().toVector(), targetLoc.toVector());

        p1.setVelocity(new Vector(dir.getX() * level, -dir.getY() / 10, dir.getZ() * level));
    }

    @Override
    public String getDescription(CommandSender sender) {
        return GKCore.instance.messageSystem.get(sender, "commands.sendTask");
    }

    @Override
    public boolean canExecute(CommandSender sender, String[] args) {
        return sender.hasPermission("gkcore.command.sendTask");
    }
}
