package com.andrei1058.handyorbs.registry;

import com.andrei1058.handyorbs.HandyOrbsPlugin;
import com.andrei1058.handyorbs.api.OrbCategory;
import com.andrei1058.handyorbs.core.OrbBase;
import com.andrei1058.handyorbs.core.model.WheatOrb;
import com.andrei1058.handyorbs.core.region.Cuboid;
import com.andrei1058.handyorbs.core.region.IRegion;
import com.andrei1058.handyorbs.database.repository.OrbRepository;
import com.google.common.annotations.Beta;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class OrbRegistry {
    private static OrbRegistry instance;

    private final HashMap<OrbCategory, OrbCategoryRegistry> orbTypeRegistry = new HashMap<>();

    private OrbRegistry() {
        //register categories
        for (OrbCategory category : OrbCategory.values()) {
            orbTypeRegistry.put(category, new OrbCategoryRegistry());
        }
        // register default orbs
        //todo check for disabled orbs
        OrbCategoryRegistry farmingRegistry = getCategoryRegistry(OrbCategory.FARMING);
        if (farmingRegistry != null) {
            farmingRegistry.addOrb("wheat", WheatOrb.class);
        }
    }

    /**
     * Init registry on plugin load.
     */
    public static void init() {
        if (instance == null) {
            instance = new OrbRegistry();
        }
    }

    /**
     * Get registry.
     */
    public static OrbRegistry getInstance() {
        return instance;
    }

    /**
     * Get registry by category.
     */
    public OrbCategoryRegistry getCategoryRegistry(OrbCategory category) {
        return orbTypeRegistry.get(category);
    }

    /**
     * Register a new orb type.
     *
     * @param identifier orb identifier.
     * @param orb        orb class.
     * @param category   orb category.
     */
    public boolean registerOrb(String identifier, Class<? extends OrbBase> orb, OrbCategory category) {
        if (identifier.length() > 20) {
            throw new IllegalArgumentException("Identifier cannot be bigger than 20");
        }
        OrbCategoryRegistry subRegistry = getCategoryRegistry(category);
        return subRegistry != null && subRegistry.addOrb(identifier, orb);
    }

    /**
     * Spawn a new orb and save it to the database.
     */
    @Nullable
    public OrbBase spawnOrb(String identifier, OrbCategory category, Location location, String regionData, Integer delay) {
        OrbCategoryRegistry subRegistry = getCategoryRegistry(category);
        if (subRegistry == null) return null;
        Class<? extends OrbBase> orb = subRegistry.getOrb(identifier);
        if (orb == null) return null;
        try {
            Constructor<?> constructor = orb.getConstructor(Location.class, IRegion.class, Integer.class);
            IRegion region = parseRegion(location, regionData);
            if (region == null) {
                HandyOrbsPlugin.getInstance().getLogger().severe("Could not spawn orb at " + location.toString() + " because region is invalid.");
                return null;
            }
            return (OrbBase) constructor.newInstance(location, region, delay);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Despawn an orb.
     */
    public void despawnOrb(OrbBase orb){
        OrbCategory orbCategory = getActiveOrbCategory(orb.getOrbId());
        if (orbCategory != null){
            OrbCategoryRegistry subRegistry = getCategoryRegistry(orbCategory);
            subRegistry.removeActiveOrb(orb.getOrbId());
        }
        orb.getOrbEntity().destroy();
    }

    /**
     * Despawn an orb.
     */
    public void despawnOrb(OrbBase orb, OrbCategory category){
        int orbId = orb.getOrbId();
        if (category != null){
            OrbCategoryRegistry subRegistry = getCategoryRegistry(category);
            subRegistry.removeActiveOrb(orbId);
        }
        Bukkit.getScheduler().runTaskAsynchronously(HandyOrbsPlugin.getInstance(),
                ()-> OrbRepository.getInstance().markOrbAsRemovedFromGround(orbId));

        orb.getOrbEntity().destroy();
    }


    @Nullable
    public OrbBase getActiveOrbById(int orbId) {
        for (OrbCategoryRegistry category : orbTypeRegistry.values()) {
            OrbBase orb = category.getActiveOrb(orbId);
            if (orb != null) {
                return orb;
            }
        }
        return null;
    }

    @Nullable
    public OrbCategory getActiveOrbCategory(int orbId) {
        for (Map.Entry<OrbCategory, OrbCategoryRegistry> entry : orbTypeRegistry.entrySet()) {
            if (entry.getValue().getActiveOrb(orbId) != null) {
                return entry.getKey();
            }
        }
        return null;
    }

    @Nullable
    public String getActiveOrbIdentifier(OrbBase orbBase) {
        for (OrbCategoryRegistry category : orbTypeRegistry.values()) {
            String orb = category.getActiveOrbType(orbBase);
            if (orb != null) {
                return orb;
            }
        }
        return null;
    }

    @Beta
    public int removeInstancesAtChunk(String world, int x, int z) {
        int removed = 0;
        for (OrbCategoryRegistry category : orbTypeRegistry.values()) {
            removed += category.removeInstancesAtChunk(world, x, z);
        }
        return removed;
    }

    @Beta
    public List<OrbBase> getActiveOrbsInChunk(String world, int x, int z) {
        List<OrbBase> orbs = new ArrayList<>();
        for (OrbCategoryRegistry category : orbTypeRegistry.values()) {
            orbs.addAll(category.getActiveOrbsInChunk(world, x, z));
        }
        return orbs;
    }

    /**
     * Get a list o f orbs by checking if the given block is in their region.
     */
    public List<OrbBase> getActiveOrbsByBlock(String world, int x, int y, int z) {
        List<OrbBase> orbs = new ArrayList<>();
        for (OrbCategoryRegistry category : orbTypeRegistry.values()) {
            orbs.addAll(category.getActiveOrbsByBlock(world, x, y, z));
        }
        return orbs;
    }

    @Beta
    @Nullable
    public IRegion parseRegion(Location orbLoc, String data) {
        String[] args = data.split(";");
        if (args.length < 2) {
            return null;
        }
        IRegion region = null;
        switch (args[0].toLowerCase()) {
            case "internal":
                if (args[1].equalsIgnoreCase("cuboid") && args.length > 2) {
                    try {
                        int radius = Integer.parseInt(args[2]);
                        region = new Cuboid(radius, orbLoc);
                    } catch (Exception ex) {
                        HandyOrbsPlugin.getInstance().getLogger().severe("Bad radius at: " + data);
                    }
                }
                break;
            case "wg":
        }
        return region;
    }
}
