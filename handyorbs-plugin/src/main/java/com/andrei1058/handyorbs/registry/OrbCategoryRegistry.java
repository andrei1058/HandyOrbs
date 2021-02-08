package com.andrei1058.handyorbs.registry;

import com.andrei1058.handyorbs.core.OrbBase;
import com.google.common.annotations.Beta;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class OrbCategoryRegistry {

    private final HashMap<String, Class<? extends OrbBase>> orbsByIdentifier = new HashMap<>();
    private final Map<Integer, OrbBase> activeOrbsById = new HashMap<>();

    public boolean addOrb(String identifier, Class<? extends OrbBase> orb) {
        if (orbsByIdentifier.containsKey(identifier)) {
            return false;
        }
        orbsByIdentifier.put(identifier, orb);
        return true;
    }

    /**
     * Remove a orb type.
     *
     * @param identifier orb identifier.
     * @param deactivate deactivate loaded orbs of given type.
     */
    public void removeOrbs(String identifier, boolean deactivate) {
        orbsByIdentifier.remove(identifier);
        //todo implement deactivate
    }

    /**
     * Check if given identifier is already used.
     */
    public boolean isRegistered(String identifier) {
        return orbsByIdentifier.containsKey(identifier);
    }

    public void addActiveOrb(int orbId, OrbBase orb) {
        if (activeOrbsById.containsKey(orbId)) {
            throw new IllegalStateException("Orb with id: " + orbId + " is already on the active orbs list.");
        }
        activeOrbsById.put(orbId, orb);
    }

    public void removeActiveOrb(int orbId) {
        activeOrbsById.remove(orbId);
    }

    /**
     * Get a orb by identifier.
     */
    @Nullable
    public Class<? extends OrbBase> getOrb(String identifier) {
        return orbsByIdentifier.get(identifier);
    }

    /**
     * Get an active orb by id.
     */
    @Nullable
    public OrbBase getActiveOrb(int orbId) {
        return activeOrbsById.get(orbId);
    }

    public String getActiveOrbType(@Nullable OrbBase orbBase) {
        for (Map.Entry<String, Class<? extends OrbBase>> types : orbsByIdentifier.entrySet()) {
            if (orbBase == null) continue;
            if (types.getValue().getName().equals(orbBase.getClass().getName())) {
                return types.getKey();
            }
        }
        return null;
    }

    @Beta
    public int removeInstancesAtChunk(String world, int x, int z) {
        List<Integer> toRemove = new ArrayList<>();
        for (Map.Entry<Integer, OrbBase> entry : activeOrbsById.entrySet()) {
            if (entry.getValue().getWorld().equals(world)) {
                if (entry.getValue().getOrbEntity().getChunkX() == x && entry.getValue().getOrbEntity().getChunkZ() == z) {
                    toRemove.add(entry.getKey());
                    Bukkit.broadcastMessage("Removed entity with id: " + entry.getKey());
                }
            }
        }

        int size = toRemove.size();
        toRemove.forEach(activeOrbsById::remove);
        return size;
    }
}
