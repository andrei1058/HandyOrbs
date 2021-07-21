package com.andrei1058.handyorbs.gui;

import com.andrei1058.handyorbs.HandyOrbsPlugin;
import com.andrei1058.handyorbs.api.locale.Locale;
import com.andrei1058.handyorbs.api.locale.Message;
import com.andrei1058.handyorbs.config.MainConfig;
import com.andrei1058.handyorbs.core.HandyOrbsCore;
import com.andrei1058.handyorbs.core.OrbBase;
import com.andrei1058.handyorbs.database.repository.OrbRepository;
import com.andrei1058.handyorbs.language.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SelfOrbGUI {

    private final Inventory inventory;
    private final Player viewer;
    private final OrbBase orbBase;
    private final int changeNameSlot = MainConfig.getConfig().getProperty(MainConfig.ORB_GUI_CHANGE_NAME_SLOT);
    private final int toggleNameSlot = MainConfig.getConfig().getProperty(MainConfig.ORB_GUI_TOGGLE_NAME_SLOT);

    public SelfOrbGUI(Player player, OrbBase orbBase) {
        this.viewer = player;
        this.orbBase = orbBase;

        Locale locale = LanguageManager.getINSTANCE().getLocale(getViewer());
        inventory = Bukkit.createInventory(new SelfOrbHolder(), InventoryType.HOPPER, locale.getMsg(getViewer(), Message.ORB_SELF_GUI_NAME)
                .replace("{orbName}", orbBase.getDisplayName()));

        ItemStack changeName = createItemStack(MainConfig.getConfig().getProperty(MainConfig.ORB_GUI_CHANGE_NAME_MATERIAL),
                locale.getMsg(getViewer(), Message.ORB_RENAME_ITEM_NAME), locale.getMsgList(getViewer(), Message.ORB_RENAME_ITEM_LORE));

        ItemStack toggleName = createItemStack(MainConfig.getConfig().getProperty(MainConfig.ORB_GUI_TOGGLE_NAME_MATERIAL),
                locale.getMsg(getViewer(), Message.ORB_TOGGLE_TOGGLE_ITEM_NAME), locale.getMsgList(getViewer(), Message.ORB_TOGGLE_NAME_ITEM_LORE));


        if (changeNameSlot >= 0 && changeNameSlot < inventory.getSize()) {
            inventory.setItem(changeNameSlot, changeName);
        }

        if (toggleNameSlot >= 0 && toggleNameSlot < inventory.getSize()) {
            inventory.setItem(toggleNameSlot, toggleName);
        }
    }

    public int getChangeNameSlot() {
        return changeNameSlot;
    }

    public int getToggleNameSlot() {
        return toggleNameSlot;
    }

    public Player getViewer() {
        return viewer;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public OrbBase getOrbBase() {
        return orbBase;
    }

    public class SelfOrbHolder implements InventoryHolder {

        @NotNull
        @Override
        public Inventory getInventory() {
            return inventory;
        }

        public void onClick(int slot) {
            if (slot == getChangeNameSlot()) {
                GUIManager.getInstance().openReName(getOrbBase(), getViewer());
            } else if (slot == getToggleNameSlot()) {
                getOrbBase().getOrbEntity().setCustomNameVisible(!getOrbBase().getOrbEntity().getCustomNameVisible());
                getViewer().closeInventory();
                Bukkit.getScheduler().runTaskAsynchronously(HandyOrbsPlugin.getInstance(),
                        () -> OrbRepository.getInstance().updateOrbNameVisibility(orbBase));
            }
        }
    }

    private static ItemStack createItemStack(String material, String displayName, List<String> lore) {
        ItemStack item = HandyOrbsCore.getInstance().getItemStackSupport()
                .createItem(material, 1, (byte) 0);
        if (item != null && item.getItemMeta() != null) {
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(displayName);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }
}
