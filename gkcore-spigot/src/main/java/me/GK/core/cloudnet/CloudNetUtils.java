package me.GK.core.cloudnet;

import de.dytanic.cloudnet.common.concurrent.ITask;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.ext.bridge.BridgePlayerManager;
import de.dytanic.cloudnet.ext.bridge.BridgeServiceProperty;
import de.dytanic.cloudnet.ext.bridge.ServiceInfoSnapshotUtil;
import de.dytanic.cloudnet.ext.bridge.player.IPlayerManager;
import me.GK.core.GKCore;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;

public class CloudNetUtils {
    public static void sendService(Player target, String serviceName){
        sendService(target.getUniqueId(), serviceName);
    }
    public static void sendService(UUID playerUUID, String serviceName){

        CloudNetDriver.getInstance().getServicesRegistry()
                .getFirstService(IPlayerManager.class).getPlayerExecutor(playerUUID).connect(serviceName);
    }
    public static void sendTask(Player target, String taskName){
        ITask<Collection<ServiceInfoSnapshot>> task = CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServicesAsync(taskName);

        Player finalPlayer = target;
        task.onComplete(new BiConsumer<ITask<Collection<ServiceInfoSnapshot>>, Collection<ServiceInfoSnapshot>>(){

            @Override
            public void accept(ITask<Collection<ServiceInfoSnapshot>> t, Collection<ServiceInfoSnapshot> u) {
                // TODO Auto-generated method stub
                int smallestPlayerAmount = 10000;
                String selectedServerName = "cannotFindService";
                for(ServiceInfoSnapshot server : u) {
                    Optional<Integer> playerAmountOptional = server.getProperty(BridgeServiceProperty.ONLINE_COUNT);
                    if(playerAmountOptional==null) continue;
                    int playerAmount = playerAmountOptional.get();
                    String serverName = server.getName();
                    if(playerAmount < smallestPlayerAmount) {
                        smallestPlayerAmount = playerAmount;
                        selectedServerName = serverName;
                    }
                }
                if(smallestPlayerAmount == 10000) {
                    finalPlayer.sendMessage(GKCore.getInstance().configSystem.get("cannotFindCloudTask"));
                }
                sendService(target, selectedServerName);

            }

        });
    }
}
