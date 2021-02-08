package com.andrei1058.handyorbs.listener;

import com.andrei1058.handyorbs.HandyOrbsPlugin;
import com.andrei1058.handyorbs.api.OrbCategory;
import com.andrei1058.handyorbs.database.OrbEntity;
import com.andrei1058.handyorbs.database.OrbRepository;
import com.andrei1058.handyorbs.registry.ChunkOrbRegistry;
import com.andrei1058.handyorbs.registry.ChunkedOrbs;
import com.andrei1058.handyorbs.registry.OrbRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.List;

public class ChunkListener implements Listener {

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event){
        ChunkedOrbs chunkedOrbs = ChunkOrbRegistry.getChunkedOrbs(event.getChunk().getX(), event.getChunk().getZ());
        if (chunkedOrbs != null){

        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event){
        Bukkit.getScheduler().runTaskAsynchronously(HandyOrbsPlugin.getInstance(), () -> {
            List<OrbEntity> models = OrbRepository.getInstance().getOrbsAtChunk(event.getChunk().getX(), event.getChunk().getZ(), event.getWorld().getName());
            Bukkit.getScheduler().runTask(HandyOrbsPlugin.getInstance(), () -> {
                for (OrbEntity model : models) {
                    Location loc = new Location(event.getWorld(), model.getLocX(), model.getLocY(), model.getLocZ());
                    OrbRegistry.getInstance().spawnOrb(model.getType(), OrbCategory.valueOf(model.getCategory()), loc, 20);
                }
            });
        });
    }
}
