package com.gkpixel.core.containers;

import com.gkpixel.core.GKCore;
import com.gkpixel.core.main.Extensions;
import com.gkpixel.core.modules.TextButtonSystem;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Parse {@link ItemStack} to JSON
 *
 * @author DevSrSouza
 * @version 1.0
 * <p>
 * https://github.com/DevSrSouza/
 * You can find updates here https://gist.github.com/DevSrSouza
 */
public class ListDisplayer {
    /////////////////////////////////////////////////////////////////////////
    //static
    private static final int defaultLinePerPage = 6;
    private static final String leftSign = "[ ◀◀ ]";
    private static final String rightSign = "[ ▶▶ ]";
    private static final String pageSign = "  &2%current%&7/&2%total%  ";
    public UUID uid;
    public String displayerName = "";
    public List<BaseComponent[]> currentDisplayingList = new ArrayList<BaseComponent[]>();
    public int linePerPage = 10;//how many line per page?
    public int currentPageID = 0;

    public ListDisplayer(UUID uid) {
        this.uid = uid;
    }

    public static void initiate() {
    }

    ///////////////////////////////////////////////////////////

    public static void displayList(UUID uid, String displayerName, List<BaseComponent[]> list, int linePerPage) {
        if (list == null) list = new ArrayList<BaseComponent[]>();
        GKPlayer GKP = GKPlayer.fromUUID(uid);
        if (GKP == null) return;
        GKP.listDisplayer.currentDisplayingList = list;
        GKP.listDisplayer.displayerName = displayerName;
        GKP.listDisplayer.linePerPage = linePerPage;
        GKP.listDisplayer.currentPageID = 0;
        GKP.listDisplayer.sendCurrentPage();
    }

    public static void displayList(UUID uid, String displayerName, List<BaseComponent[]> list) {
        displayList(uid, displayerName, list, defaultLinePerPage);
    }

    public static void displayList(Player player, String displayerName, List<BaseComponent[]> list, int linePerPage) {
        UUID uid = player.getUniqueId();
        displayList(uid, displayerName, list, linePerPage);
    }
    ///////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////
    //Sending

    public static void displayList(Player player, String displayerName, List<BaseComponent[]> list) {
        UUID uid = player.getUniqueId();
        displayList(uid, displayerName, list, defaultLinePerPage);
    }

    public static List<BaseComponent[]> getDisplayingList(Player player) {
        GKPlayer GKP = GKPlayer.fromPlayer(player);
        if (GKP == null) return null;
        return GKP.listDisplayer.currentDisplayingList;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uid);
    }

    private void debug(String str) {
        ////System.out.print(str);
    }
    ///////////////////////////////////////////////////////////

    private void setHoverDescription(BaseComponent component, String str, ChatColor color) {
        ArrayList<BaseComponent> componentList = new ArrayList<BaseComponent>();
        BaseComponent description = new TextComponent(str);
        description.setColor(color);
        componentList.add(description);
        BaseComponent[] baseComponentArray = new BaseComponent[componentList.size()];
        for (int i = 0; i < componentList.size(); i++) {
            baseComponentArray[i] = componentList.get(i);
        }
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, baseComponentArray));

    }

    private void setHoverDescription(BaseComponent[] components, String str, ChatColor color) {
        ArrayList<BaseComponent> componentList = new ArrayList<BaseComponent>();
        BaseComponent description = new TextComponent(str);
        description.setColor(color);
        componentList.add(description);
        BaseComponent[] baseComponentArray = new BaseComponent[componentList.size()];
        for (int i = 0; i < componentList.size(); i++) {
            baseComponentArray[i] = componentList.get(i);
        }
        for (BaseComponent component : components) {
            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, baseComponentArray));

        }

    }

    ///////////////////////////////////////////////////////////
    //Command callback
    public void ChangePage(int pageID) {
        if (pageID < currentDisplayingList.size()) {
            currentPageID = pageID;
        }
        sendCurrentPage();
    }

    ///////////////////////////////////////////////////////////
    //get Components
    private BaseComponent[] getLeftSignComponent(int pageID, boolean available) {
        TextComponent add = new TextComponent(leftSign);
        if (available) {
            add.setColor(ChatColor.GOLD);
        } else
            add.setColor(ChatColor.GRAY);
        add.setBold(true);
        return TextButtonSystem.instance.generateCallbackTextButton(getPlayer(), () -> ChangePage(pageID - 1), add.toLegacyText(), GKCore.instance.messageSystem.get(getPlayer(), "previousPage"));
    }

    private BaseComponent[] getRightSignComponent(int pageID, boolean available) {
        TextComponent add = new TextComponent(rightSign);
        if (available) {
            add.setColor(ChatColor.GOLD);
        } else
            add.setColor(ChatColor.GRAY);
        add.setBold(true);
        return TextButtonSystem.instance.generateCallbackTextButton(getPlayer(), () -> ChangePage(pageID + 1), add.toLegacyText(), GKCore.instance.messageSystem.get(getPlayer(), "nextPage"));
    }
    /////////////////////////////////////////////////////////////////////////

    private BaseComponent[] getPageSignComponent(int pageID, int totalPage) {
        String str = pageSign;
        str = Extensions.color(str);
        str = str.replace("%current%", "" + (pageID + 1)).replace("%total%", "" + totalPage);
        BaseComponent[] page = TextComponent.fromLegacyText(str);
        setHoverDescription(page, "Page " + pageID, ChatColor.YELLOW);
        return page;
    }

    ///////////////////////////////////////////////////////////
    //Current Page
    private List<BaseComponent[]> getCurrentPage() {

        return getPage(currentDisplayingList, linePerPage, currentPageID);
    }

    private void sendDisplayerName(Player player) {
        player.sendMessage(Extensions.color(GKCore.instance.messageSystem.get("editorNameLine")).replace("%name%", displayerName));
    }

    private void sendCurrentBottomTool() {
        Player player = getPlayer();
        TextComponent line = new TextComponent("");
        BaseComponent[] leftSign = this.getLeftSignComponent(currentPageID, (currentPageID > 0));
        BaseComponent[] pageSign = this.getPageSignComponent(currentPageID, getCurrentTotalPage());
        BaseComponent[] rightSign = this.getRightSignComponent(currentPageID, ((currentPageID + 1) < getCurrentTotalPage()));
        for (BaseComponent component : leftSign) {
            line.addExtra(component);
        }
        for (BaseComponent component : pageSign) {
            line.addExtra(component);
        }
        for (BaseComponent component : rightSign) {
            line.addExtra(component);
        }
        player.spigot().sendMessage(line);
    }

    private void sendCurrentPage() {
        Player player = getPlayer();
        List<BaseComponent[]> page = getCurrentPage();
        sendDisplayerName(player);
        for (BaseComponent[] line : page) {
            player.spigot().sendMessage(line);
        }
        if (getCurrentTotalPage() > 1) {
            sendCurrentBottomTool();
        }
        GKCore.instance.messageSystem.send(getPlayer(), "bottomSplitter");
    }

    /////////////////////////////////////////////////////////////////////////
    //useful public
    public List<BaseComponent[]> stringListToComponentList(List<String> list) {
        List<BaseComponent[]> result = new ArrayList<BaseComponent[]>();
        for (String str : list) {
            BaseComponent[] text = TextComponent.fromLegacyText(str);
            result.add(text);
        }
        return result;
    }

    /////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////////////
    //Tools
    private List<List<BaseComponent[]>> getPageList(List<BaseComponent[]> list, int linePerPage) {
        List<List<BaseComponent[]>> pageList = new ArrayList<List<BaseComponent[]>>();

        List<BaseComponent[]> page = new ArrayList<BaseComponent[]>();


        int i = 0;
        for (BaseComponent[] line : list) {
            //debug("added '"+line.toString()+"' into page "+pageList.size());
            page.add(line);
            if (i >= linePerPage) {
                pageList.add(new ArrayList<BaseComponent[]>(page));
                page = new ArrayList<BaseComponent[]>();
                i = 0;
            } else {
                i++;
            }
        }
        if (page.size() > 0) pageList.add(page);

        return new ArrayList<List<BaseComponent[]>>(pageList);
    }

    private List<BaseComponent[]> getPage(List<BaseComponent[]> list, int linePerPage, int pageID) {
        //debug("getting page: "+pageID);
        List<List<BaseComponent[]>> pageList = getPageList(list, linePerPage);
        if (pageID >= pageList.size()) return null;
        //debug("got page, count: "+pageList.get(pageID).size());
        return pageList.get(pageID);
    }

    private int getCurrentTotalPage() {
        List<List<BaseComponent[]>> pageList = getPageList(currentDisplayingList, linePerPage);
        return pageList.size();
    }
    /////////////////////////////////////////////////////////////////////////


}