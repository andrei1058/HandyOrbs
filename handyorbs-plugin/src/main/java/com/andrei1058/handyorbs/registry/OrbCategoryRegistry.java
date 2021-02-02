package com.andrei1058.handyorbs.registry;

import com.andrei1058.handyorbs.core.OrbBase;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.WeakHashMap;

class OrbCategoryRegistry {

    private final HashMap<String, Class<? extends OrbBase>> orbsByIdentifier = new HashMap<>();
    private final Map<Integer, OrbBase> activeOrbsById = new TreeMap<>();

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
}
