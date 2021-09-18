package com.gkpixel.core.managers.offlinecommand;

import com.gkpixel.core.GKCore;
import com.gkpixel.core.containers.GKPlayer;
import com.gkpixel.core.main.Extensions;
import com.gkpixel.core.managers.cloudnet.CloudNetUtils;
import com.google.gson.annotations.Expose;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class OfflineCommand {
    @Expose
    public List<String> availableTaskNameList = new ArrayList<>();
    @Expose
    public int delayTicks = 0;
    @Expose
    public String command;

    public OfflineCommand(String cmd) {
        command = cmd;
        if (CloudNetUtils.hasCloudNet()) {
            availableTaskNameList.add(CloudNetUtils.getCurrentTaskName());
        } else {
            availableTaskNameList.add("all");
        }
    }

    public OfflineCommand(int delayTicks, String cmd) {
        this.delayTicks = delayTicks;
        command = cmd;
        if (CloudNetUtils.hasCloudNet()) {
            availableTaskNameList.add(CloudNetUtils.getCurrentTaskName());
        } else {
            availableTaskNameList.add("all");
        }
    }

    /**
     * Try to run the command for player. Will check the required task name matches or not.
     *
     * @param player
     * @return
     */
    public boolean tryRun(Player player, Runnable callback) {
        BukkitTask task = new BukkitRunnable() {

            @Override
            public void run() {
                if (player == null)
                    return;

                //check player online
                if (!player.isOnline())
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

    public void forceRun(Player player) {
        String cmd = command.replace("%player%", player.getName());
        Extensions.runServerCommand(cmd);
    }

}
