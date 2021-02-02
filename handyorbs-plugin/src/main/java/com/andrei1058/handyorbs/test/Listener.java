package com.andrei1058.handyorbs.test;

import com.andrei1058.handyorbs.HandyOrbsPlugin;
import com.andrei1058.handyorbs.api.OrbCategory;
import com.andrei1058.handyorbs.core.OrbBase;
import com.andrei1058.handyorbs.database.OrbModel;
import com.andrei1058.handyorbs.database.StorageManager;
import com.andrei1058.handyorbs.registry.OrbRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.List;

public class Listener implements org.bukkit.event.Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLater(HandyOrbsPlugin.getInstance(), () -> {
            OrbBase orb = OrbRegistry.getInstance().spawnOrb("wheat", OrbCategory.FARMING, event.getPlayer().getLocation(), 20);
            if (orb == null) {
                event.getPlayer().sendMessage("Orb is null");
            } else {
                Bukkit.getScheduler().runTaskAsynchronously(HandyOrbsPlugin.getInstance(), () -> {
                    event.getPlayer().sendMessage("saving data");
                    StorageManager.getInstance().saveUpdate(orb);
                    int orbId = orb.getOrbId();
                });
                //Bukkit.getScheduler().runTaskTimer(HandyOrbsPlugin.getInstance(), orb::tickAnimation, 100L, 1L);
            }
        }, 40L);
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(HandyOrbsPlugin.getInstance(), () -> {
            List<OrbModel> models = StorageManager.getInstance().getOrbsAtChunk(event.getChunk().getX(), event.getChunk().getZ(), event.getWorld().getName());
            Bukkit.getScheduler().runTask(HandyOrbsPlugin.getInstance(), () -> {
                for (OrbModel model : models) {
                    Location loc = new Location(event.getWorld(), model.getLocX(), model.getLocY(), model.getLocZ());
                    OrbRegistry.getInstance().spawnOrb("wheat", OrbCategory.FARMING, loc, 20);
                }
            });
        });
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        // remove from existing lists?!
    }
}
