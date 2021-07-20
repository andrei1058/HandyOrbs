package com.andrei1058.handyorbs.gui;

import com.andrei1058.handyorbs.HandyOrbsPlugin;
import com.andrei1058.handyorbs.core.OrbBase;
import com.andrei1058.handyorbs.core.model.Ownable;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GUIManager {

    private static GUIManager instance;

    /**
     * Initialize GUI Handler.
     */
    public static void init() {
        if (instance == null) {
            instance = new GUIManager();
        }
    }

    public static GUIManager getInstance() {
        return instance;
    }

    private GUIManager() {
        Bukkit.getPluginManager().registerEvents(new OrbGUIHandler(), HandyOrbsPlugin.getInstance());
    }

    /**
     * Open orbs gui.
     * Use different params to show someone else's orbs.
     *
     * @param receiver is the GUI receiver.
     * @param owner    is the orbs owner.
     */
    public void openSelf(Player receiver, Player owner) {

    }

    public void openOrbGUI(OrbBase orbBase, Player viewer) {
        if (orbBase instanceof Ownable) {
            if (viewer.getUniqueId().equals(((Ownable) orbBase).getOwner())) {
                SelfOrbGUI gui = new SelfOrbGUI(viewer, orbBase);
                viewer.openInventory(gui.getInventory());
            }
        }
    }

    public void openReName(OrbBase orbBase, Player owner) {
        new AnvilGUI.Builder()
                .onComplete((player, text) -> {                                    //called when the inventory output slot is clicked
                    orbBase.getOrbEntity().setDisplayName(ChatColor.translateAlternateColorCodes('&', text));
                    return AnvilGUI.Response.close();
                })
//                .preventClose()                                                    //prevents the inventory from being closed
                //todo locale
                .text("Type in the new name")                              //sets the text the GUI should start with
                .itemLeft(new ItemStack(Material.IRON_SWORD))                      //use a custom item for the first slot
                .itemRight(new ItemStack(Material.IRON_SWORD))                     //use a custom item for the second slot
                .onLeftInputClick(player -> player.sendMessage("first sword"))     //called when the left input slot is clicked
                .onRightInputClick(player -> player.sendMessage("second sword"))   //called when the right input slot is clicked
                .title("Enter your answer.")                                       //set the title of the GUI (only works in 1.14+)
                .plugin(HandyOrbsPlugin.getInstance())                                          //set the plugin instance
                .open(owner);
    }
}
