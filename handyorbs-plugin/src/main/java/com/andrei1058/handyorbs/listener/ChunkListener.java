package com.andrei1058.handyorbs.listener;

import com.andrei1058.handyorbs.HandyOrbsPlugin;
import com.andrei1058.handyorbs.api.OrbCategory;
import com.andrei1058.handyorbs.core.OrbBase;
import com.andrei1058.handyorbs.core.model.Ownable;
import com.andrei1058.handyorbs.database.model.OrbEntity;
import com.andrei1058.handyorbs.database.repository.OrbRepository;
import com.andrei1058.handyorbs.registry.OrbCategoryRegistry;
import com.andrei1058.handyorbs.registry.OrbRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

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
                    OrbBase orb = OrbRegistry.getInstance().spawnOrb(model.getType(), orbCategory, loc, model.getRegion(), model.getActivityDelay());
                    if (orb != null) {
                        orb.setOrbId(model.getOrbId());
                        OrbCategoryRegistry registry = OrbRegistry.getInstance().getCategoryRegistry(orbCategory);

                        // initialize and register orb
                        orb.getOrbEntity().setDisplayName(model.getDisplayName());
                        orb.getOrbEntity().setCustomNameVisible(model.isNameStatus());
                        orb.getOrbEntity().setRightClickListener(OrbRightClickHandler.getInstance().getDefaultRightClickListener(orb));
                        if (orb instanceof Ownable){
                            ((Ownable) orb).setOwner(model.getOwner());
                        }
                        registry.addActiveOrb(orb.getOrbId(), orb);
                    }
                }
            });
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChunkUnload(ChunkUnloadEvent event) {
        // remove instances from existing lists
        int amount = OrbRegistry.getInstance().removeInstancesAtChunk(event.getWorld().getName(), event.getChunk().getX(), event.getChunk().getZ());
        if (amount != 0) {
            HandyOrbsPlugin.log("Removed " + amount + " orb instances at: " + event.getWorld().getName() + " X:" + event.getChunk().getX() +
                    " Z:" + event.getChunk().getZ());
        }
    }
}
