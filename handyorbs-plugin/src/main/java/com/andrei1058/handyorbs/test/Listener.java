package com.andrei1058.handyorbs.test;

import com.andrei1058.handyorbs.HandyOrbsPlugin;
import com.andrei1058.handyorbs.api.OrbCategory;
import com.andrei1058.handyorbs.core.OrbBase;
import com.andrei1058.handyorbs.database.OrbRepository;
import com.andrei1058.handyorbs.registry.OrbRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

public class Listener implements org.bukkit.event.Listener {

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        // remove from existing lists?!
        //todo needs a better system, why do I even keep active orbs in a list? Think about it
        int amount = OrbRegistry.getInstance().removeInstancesAtChunk(event.getWorld().getName(), event.getChunk().getX(), event.getChunk().getZ());
        if (amount != 0) {
            Bukkit.broadcastMessage("Removed " + amount + " orb instances at: " + event.getWorld().getName() + " X:" + event.getChunk().getX() +
                    " Z:" + event.getChunk().getZ());
        }
    }

    @EventHandler
    public void onItem(PlayerInteractEvent event) {
        if (event.getItem() == null) return;
        if (event.getItem().getType() == Material.DIAMOND) {
            if (event.getPlayer().hasCooldown(event.getItem().getType())) return;
            event.getPlayer().setCooldown(event.getItem().getType(), 100);
            OrbBase orb = OrbRegistry.getInstance().spawnOrb("wheat", OrbCategory.FARMING, event.getPlayer().getLocation(), 20);
            if (orb != null) {
                Bukkit.getScheduler().runTaskAsynchronously(HandyOrbsPlugin.getInstance(), () -> {
                    OrbRepository.getInstance().saveUpdate(orb, OrbCategory.FARMING);
                    int orbId = orb.getOrbId();
                    event.getPlayer().sendMessage("Spawned orb with ID: " + orbId);
                });
            }
        }
    }
}
