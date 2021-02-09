package com.andrei1058.handyorbs.listener;

import com.andrei1058.handyorbs.HandyOrbsPlugin;
import com.andrei1058.handyorbs.api.OrbCategory;
import com.andrei1058.handyorbs.core.OrbBase;
import com.andrei1058.handyorbs.database.OrbEntity;
import com.andrei1058.handyorbs.database.OrbRepository;
import com.andrei1058.handyorbs.registry.OrbCategoryRegistry;
import com.andrei1058.handyorbs.registry.OrbRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import java.util.List;

public class ChunkListener implements Listener {

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(HandyOrbsPlugin.getInstance(), () -> {
            List<OrbEntity> models = OrbRepository.getInstance().getOrbsAtChunk(event.getChunk().getX(), event.getChunk().getZ(), event.getWorld().getName());
            Bukkit.getScheduler().runTask(HandyOrbsPlugin.getInstance(), () -> {
                for (OrbEntity model : models) {
                    Location loc = new Location(event.getWorld(), model.getLocX(), model.getLocY(), model.getLocZ());
                    OrbCategory orbCategory = OrbCategory.valueOf(model.getCategory());
                    //todo implement delay
                    OrbBase orb = OrbRegistry.getInstance().spawnOrb(model.getType(), orbCategory, loc, model.getRegion(), 100);
                    if (orb != null) {
                        orb.setOrbId(model.getOrbId());
                        OrbCategoryRegistry registry = OrbRegistry.getInstance().getCategoryRegistry(orbCategory);
                        registry.addActiveOrb(orb.getOrbId(), orb);
                        Bukkit.broadcastMessage("Spawning orb with ID: " + model.getOrbId());
                    }
                }
            });
        });
    }
}
