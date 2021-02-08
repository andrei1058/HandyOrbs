package com.andrei1058.handyorbs.test;

import com.andrei1058.handyorbs.HandyOrbsPlugin;
import com.andrei1058.handyorbs.api.OrbCategory;
import com.andrei1058.handyorbs.core.OrbBase;
import com.andrei1058.handyorbs.database.OrbRepository;
import com.andrei1058.handyorbs.registry.OrbRegistry;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

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
                    OrbRepository.getInstance().saveUpdate(orb);
                    int orbId = orb.getOrbId();
                });
                //Bukkit.getScheduler().runTaskTimer(HandyOrbsPlugin.getInstance(), orb::tickAnimation, 100L, 1L);
            }
        }, 40L);
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        // remove from existing lists?!
    }
}
