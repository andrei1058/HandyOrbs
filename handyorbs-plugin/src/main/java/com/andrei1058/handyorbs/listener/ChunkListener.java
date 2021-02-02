package com.andrei1058.handyorbs.listener;

import com.andrei1058.handyorbs.registry.ChunkOrbRegistry;
import com.andrei1058.handyorbs.registry.ChunkedOrbs;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

public class ChunkListener implements Listener {

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event){
        ChunkedOrbs chunkedOrbs = ChunkOrbRegistry.getChunkedOrbs(event.getChunk().getX(), event.getChunk().getZ());
        if (chunkedOrbs != null){

        }
    }
}
