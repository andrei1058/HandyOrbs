package com.andrei1058.handyorbs.gui;

import com.andrei1058.handyorbs.HandyOrbsPlugin;
import com.andrei1058.handyorbs.config.MainConfig;
import com.andrei1058.handyorbs.core.HandyOrbsCore;
import com.andrei1058.handyorbs.core.OrbBase;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SelfOrbGUI {

    private final Inventory inventory;
    private final Player viewer;
    private final OrbBase orbBase;
    private final int changeNameSlot = MainConfig.getConfig().getProperty(MainConfig.CHANGE_NAME_SLOT);

    public SelfOrbGUI(Player player, OrbBase orbBase) {
        this.viewer = player;
        this.orbBase = orbBase;

        inventory = Bukkit.createInventory(new SelfOrbHolder(), InventoryType.HOPPER, "SODMKASNDOJANIP");

        ItemStack changeName = HandyOrbsCore.getInstance().getItemStackSupport()
                .createItem(MainConfig.getConfig().getProperty(MainConfig.CHANGE_NAME_MATERIAL), 1, (byte) 0);

        if (changeNameSlot >= 0 && changeNameSlot < inventory.getSize()) {
            inventory.setItem(changeNameSlot, changeName);
        }
    }

    public int getChangeNameSlot() {
        return changeNameSlot;
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

        public void onClick(int slot){
            if (slot == getChangeNameSlot()){
                GUIManager.getInstance().openReName(getOrbBase(), getViewer());
            }
        }
    }
}
