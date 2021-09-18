package gk.minuskube.inv;

import com.gkpixel.core.GKCore;
import gk.minuskube.inv.content.InventoryContents;
import gk.minuskube.inv.content.InventoryProvider;
import gk.minuskube.inv.opener.InventoryOpener;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unchecked")
public class SmartInventory {

    private final InventoryManager manager;
    private String id;
    private String title;
    private InventoryType type;
    private int rows, columns;
    private boolean closeable;
    private InventoryProvider provider;
    private SmartInventory parent;
    private List<InventoryListener<? extends Event>> listeners;

    private SmartInventory(InventoryManager manager) {
        this.manager = manager;
    }

    public static Builder builder() {
        return new Builder();
    }

    public void updateInv(Player player, String title, int size) throws NoSuchFieldException, SecurityException {
    	/*CraftPlayer cp = (CraftPlayer) player;
    	EntityPlayer ep = (EntityPlayer) cp.getHandle();

    	Field f = ep.getClass().getDeclaredField("containerCounter");
    	f.setAccessible(true);
    	Integer containerCounter;
		try {
			containerCounter = (Integer)f.get(ep);
			if(containerCounter != null)
	    	{
	    	    ProtocolManager manager = ProtocolLibrary.getProtocolManager();
	    	    final PacketContainer packet = manager.createPacket(PacketType.Play.Server.OPEN_WINDOW);
	    	    packet.getIntegers().write(0, containerCounter);
	    	    packet.getStrings().write(0, "minecraft:container");
	    	    packet.getChatComponents().write(0, WrappedChatComponent.fromJson("{\"text\": \"" + title + "\"}"));
	    	    packet.getIntegers().write(1, size);
	    	    try
	    	    {
	    	        manager.sendServerPacket(player, packet);
	    	        // One downside: the clientside Inventory will be empty. Luckily,
	    	        // this allows up to force Spigot to update this in our stead :P
	    	        player.updateInventory();
	    	    }
	    	    catch (InvocationTargetException ex)
	    	    {
	    	        ex.printStackTrace();
	    	    }
	    	}
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

    }

    public Inventory open(Player player) {
        return open(player, 0);
    }

    public Inventory open(Player player, int page) {
        Optional<SmartInventory> oldInv = this.manager.getInventory(player);

        /*oldInv.ifPresent(inv -> {
        	InventoryProvider oldProvider = inv.getProvider();

            inv.getListeners().stream()
                    .filter(listener -> listener.getType() == InventoryCloseEvent.class)
                    .forEach(listener -> ((InventoryListener<InventoryCloseEvent>) listener)
                            .accept(new InventoryCloseEvent(player.getOpenInventory())));

            this.manager.setInventory(player, null);
        });*/
        if (oldInv.isPresent()) {
            SmartInventory old = oldInv.get();
            this.manager.setInventory(player, null);
            if (old.getRows() == getRows() && old.getColumns() == getColumns() && old.getType() == getType()) {
                //use same menu, but change content
                try {
                    updateInv(player, title, getRows() * getColumns());
                } catch (NoSuchFieldException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (SecurityException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                InventoryContents contents = new InventoryContents.Impl(this, player);
                contents.pagination().page(page);

                this.manager.setContents(player, contents);
                this.provider.init(player, contents);
                InventoryOpener opener = this.manager.findOpener(type)
                        .orElseThrow(() -> new IllegalStateException("No opener found for the inventory type " + type.name()));
                Inventory handle = opener.update(this, player);

                //player.sendMessage("using old menu, changing contents");
                this.manager.setInventory(player, this);

                return handle;
            }
        }
        {
            //player.sendMessage("using new menu, resetting mouse position");
            //else use a new menu instead
            this.manager.setInventory(player, null);
            InventoryContents contents = new InventoryContents.Impl(this, player);
            contents.pagination().page(page);

            this.manager.setContents(player, contents);
            this.provider.init(player, contents);

            InventoryOpener opener = this.manager.findOpener(type)
                    .orElseThrow(() -> new IllegalStateException("No opener found for the inventory type " + type.name()));
            Inventory handle = opener.open(this, player);

            this.manager.setInventory(player, this);

            return handle;
        }


    }

    @SuppressWarnings("unchecked")
    public void close(Player player) {
        listeners.stream()
                .filter(listener -> listener.getType() == InventoryCloseEvent.class)
                .forEach(listener -> ((InventoryListener<InventoryCloseEvent>) listener)
                        .accept(new InventoryCloseEvent(player.getOpenInventory())));

        this.manager.setInventory(player, null);
        player.closeInventory();

        this.manager.setContents(player, null);
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public InventoryType getType() {
        return type;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public boolean isCloseable() {
        return closeable;
    }

    public void setCloseable(boolean closeable) {
        this.closeable = closeable;
    }

    public InventoryProvider getProvider() {
        return provider;
    }

    public Optional<SmartInventory> getParent() {
        return Optional.ofNullable(parent);
    }

    public InventoryManager getManager() {
        return manager;
    }

    List<InventoryListener<? extends Event>> getListeners() {
        return listeners;
    }

    public static final class Builder {

        private final List<InventoryListener<? extends Event>> listeners = new ArrayList<>();
        private String id = "unknown";
        private String title = "";
        private InventoryType type = InventoryType.CHEST;
        private int rows = 6, columns = 9;
        private boolean closeable = true;
        private InventoryManager manager;
        private InventoryProvider provider;
        private SmartInventory parent;

        private Builder() {
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder type(InventoryType type) {
            this.type = type;
            return this;
        }

        public Builder size(int rows, int columns) {
            this.rows = rows;
            this.columns = columns;
            return this;
        }

        public Builder closeable(boolean closeable) {
            this.closeable = closeable;
            return this;
        }

        public Builder provider(InventoryProvider provider) {
            this.provider = provider;
            return this;
        }

        public Builder parent(SmartInventory parent) {
            this.parent = parent;
            return this;
        }

        public Builder listener(InventoryListener<? extends Event> listener) {
            this.listeners.add(listener);
            return this;
        }

        public Builder manager(InventoryManager manager) {
            this.manager = manager;
            return this;
        }

        public SmartInventory build() {
            if (this.provider == null)
                throw new IllegalStateException("The provider of the SmartInventory.Builder must be set.");

            InventoryManager manager = this.manager != null ? this.manager : GKCore.instance.invManager;

            if (manager == null)
                throw new IllegalStateException("The manager of the SmartInventory.Builder must be set, "
                        + "or the SmartInvs should be loaded as a plugin.");

            SmartInventory inv = new SmartInventory(manager);
            inv.id = this.id;
            inv.title = this.title;
            inv.type = this.type;
            inv.rows = this.rows;
            inv.columns = this.columns;
            inv.closeable = this.closeable;
            inv.provider = this.provider;
            inv.parent = this.parent;
            inv.listeners = this.listeners;

            return inv;
        }
    }

}