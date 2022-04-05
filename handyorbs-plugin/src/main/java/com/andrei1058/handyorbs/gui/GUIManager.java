package com.andrei1058.handyorbs.gui;

import com.andrei1058.handyorbs.HandyOrbsPlugin;
import com.andrei1058.handyorbs.api.locale.Message;
import com.andrei1058.handyorbs.config.MainConfig;
import com.andrei1058.handyorbs.core.OrbBase;
import com.andrei1058.handyorbs.core.model.Ownable;
import com.andrei1058.handyorbs.database.repository.OrbRepository;
import com.andrei1058.handyorbs.language.LanguageManager;
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
                .onComplete((player, text) -> {
                    int charLength = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', text)).length();
                    if (charLength > MainConfig.getConfig().getProperty(MainConfig.ORB_NAME_CHAR_LIMIT)){
                        return AnvilGUI.Response.close();
                    }
                    orbBase.getOrbEntity().setDisplayName(ChatColor.translateAlternateColorCodes('&', text));
                    OrbRepository.getInstance().updateOrbName(orbBase);
                    return AnvilGUI.Response.close();
                })

                .text(orbBase.getDisplayName())
                .itemLeft(new ItemStack(Material.NETHERITE_HOE))
                .itemRight(new ItemStack(Material.NETHERITE_HOE))
                .title(LanguageManager.getINSTANCE().getMsg(owner, Message.ORB_RENAME_GUI_NAME))
                .plugin(HandyOrbsPlugin.getInstance())
                .open(owner);
    }
}
