package com.andrei1058.handyorbs.gui;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;

public class OrbGUIHandler implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getCurrentItem() == null) return;
        if (e.getClickedInventory() == null) return;
        if (e.getClickedInventory().getHolder() == null) return;
        if (e.getClickedInventory().getHolder() instanceof SelfOrbGUI.SelfOrbHolder) {
            e.setCancelled(true);
            ((SelfOrbGUI.SelfOrbHolder) e.getClickedInventory().getHolder()).onClick(e.getSlot());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemMove(InventoryMoveItemEvent e) {
        if ((e.getDestination().getHolder() instanceof SelfOrbGUI.SelfOrbHolder
                || e.getSource().getHolder() instanceof SelfOrbGUI.SelfOrbHolder)) {
            e.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDrag(InventoryDragEvent e) {
        if (e.getInventory().getHolder() instanceof SelfOrbGUI.SelfOrbHolder) {
            e.setCancelled(true);
        }
    }
}
