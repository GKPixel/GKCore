package me.GK.core.managers.offlinecommand;

import com.google.gson.annotations.Expose;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import me.GK.core.GKCore;
import me.GK.core.main.Extensions;
import me.GK.core.managers.cloudnet.CloudNetUtils;
import me.GK.core.modules.storingsystem.StorableObject;
import me.GK.core.modules.storingsystem.StorableObjectDatabase;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class OfflineCommandData extends StorableObject {
    @Expose
    public UUID uuid;
    @Expose
    public List<OfflineCommand> commands = new ArrayList<>();

    @Override
    public StorableObjectDatabase<? extends StorableObject> getDatabase() {
        return OfflineCommandDatabase.instance;
    }

    @Override
    public String getID() {
        return uuid.toString();
    }

    public OfflineCommandData(UUID uuid){
        this.uuid = uuid;
        save();
    }

    /**
     * run all commands for player (when he is online)
     */
    public void runAllAwaitingCommands(Player player){
        //clone to prevent bug : removing the item in list while loop the list itself
        List<OfflineCommand> clonedCommands = new ArrayList<>();
        clonedCommands.addAll(commands);

        //run commands
        for(OfflineCommand offlineCommand : clonedCommands){
            offlineCommand.tryRun(player, ()->{
                //only if success, use lambda because delay ticks need callback.
                commands.remove(offlineCommand);
                save();
            });
        }
    }

    public void addAwaitingCommand(String cmd){
        OfflineCommand offlineCommand = new OfflineCommand(cmd);
        commands.add(offlineCommand);
        save();
    }
    public void addAwaitingCommand(int delayTicks, String cmd){
        OfflineCommand offlineCommand = new OfflineCommand(delayTicks, cmd);
        commands.add(offlineCommand);
        save();
    }
}
