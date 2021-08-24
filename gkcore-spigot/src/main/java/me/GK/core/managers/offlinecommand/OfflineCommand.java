package me.GK.core.managers.offlinecommand;

import com.google.gson.annotations.Expose;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import me.GK.core.GKCore;
import me.GK.core.containers.GKPlayer;
import me.GK.core.main.Extensions;
import me.GK.core.managers.cloudnet.CloudNetUtils;
import me.GK.core.modules.GKPlayerDatabase;
import me.GK.core.modules.storingsystem.StorableObject;
import me.GK.core.modules.storingsystem.StorableObjectDatabase;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class OfflineCommand{
    @Expose
    public List<String> availableTaskNameList = new ArrayList<>();
    @Expose
    public int delayTicks = 0;
    @Expose
    public String command;
    public OfflineCommand(String cmd){
        command = cmd;
        if(CloudNetUtils.hasCloudNet()) {
            availableTaskNameList.add(CloudNetUtils.getCurrentTaskName());
        }else{
            availableTaskNameList.add("all");
        }
    }
    public OfflineCommand(int delayTicks, String cmd){
        this.delayTicks = delayTicks;
        command = cmd;
        if(CloudNetUtils.hasCloudNet()) {
            availableTaskNameList.add(CloudNetUtils.getCurrentTaskName());
        }else{
            availableTaskNameList.add("all");
        }
    }

    /**
     * Try to run the command for player. Will check the required task name matches or not.
     * @param player
     * @return
     */
    public boolean tryRun(Player player, Runnable callback){
        BukkitTask task = new BukkitRunnable(){

            @Override
            public void run() {
                if(player == null)
                    return;

                //check player online
                if(!player.isOnline())
                    return;

                //Main
                forceRun(player);
                callback.run();
            }
        }.runTaskLater(GKCore.plugin, delayTicks);

        //cancel task
        GKPlayer gkp = GKPlayer.fromPlayer(player);
        gkp.cancelTaskWhenOffline(task);
        return false;
    }
    public void forceRun(Player player){
        String cmd = command.replace("%player%", player.getName());
        Extensions.runServerCommand(cmd);
    }

}
