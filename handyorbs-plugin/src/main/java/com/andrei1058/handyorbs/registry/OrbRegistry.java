package com.andrei1058.handyorbs.registry;

import com.andrei1058.handyorbs.HandyOrbsPlugin;
import com.andrei1058.handyorbs.api.OrbCategory;
import com.andrei1058.handyorbs.api.OrbDefaultsProvider;
import com.andrei1058.handyorbs.config.types.CarrotOrbConfig;
import com.andrei1058.handyorbs.config.types.WheatOrbConfig;
import com.andrei1058.handyorbs.core.HandyOrbsCore;
import com.andrei1058.handyorbs.core.OrbBase;
import com.andrei1058.handyorbs.core.model.CarrotOrb;
import com.andrei1058.handyorbs.core.model.Ownable;
import com.andrei1058.handyorbs.core.model.TimedOrb;
import com.andrei1058.handyorbs.core.model.WheatOrb;
import com.andrei1058.handyorbs.core.region.Cuboid;
import com.andrei1058.handyorbs.core.region.IRegion;
import com.andrei1058.handyorbs.database.model.OrbEntity;
import com.andrei1058.handyorbs.database.repository.OrbRepository;
import com.andrei1058.handyorbs.listener.OrbRightClickHandler;
import com.google.common.annotations.Beta;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
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
            farmingRegistry.addOrb("wheat", WheatOrb.class,
                    new OrbDefaultsProvider() {
                        @Override
                        public int getDefaultActivityDelay() {
                            return WheatOrbConfig.getConfig().getProperty(WheatOrbConfig.PLANT_DELAY);
                        }

                        @Override
                        public String getDefaultRegionString() {
                            return "internal;cuboid;" + WheatOrbConfig.getConfig().getProperty(WheatOrbConfig.INTERNAL_REGION_SIZE);
                        }

                        @Override
                        public String getDefaultDisplayName() {
                            return WheatOrbConfig.getConfig().getProperty(WheatOrbConfig.PLAYER_ORB_NAME);
                        }

                        @Override
                        public ItemStack getDefaultIcon() {
                            return WheatOrbConfig.getCachedItemStack();
                        }
                    });

            farmingRegistry.addOrb("carrot", CarrotOrb.class,
                    new OrbDefaultsProvider() {
                        @Override
                        public int getDefaultActivityDelay() {
                            return CarrotOrbConfig.getConfig().getProperty(CarrotOrbConfig.PLANT_DELAY);
                        }

                        @Override
                        public String getDefaultRegionString() {
                            return "internal;cuboid;" + CarrotOrbConfig.getConfig().getProperty(CarrotOrbConfig.INTERNAL_REGION_SIZE);
                        }

                        @Override
                        public String getDefaultDisplayName() {
                            return CarrotOrbConfig.getConfig().getProperty(CarrotOrbConfig.PLAYER_ORB_NAME);
                        }

                        @Override
                        public ItemStack getDefaultIcon() {
                            return CarrotOrbConfig.getCachedItemStack();
                        }
                    });
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
     * @param identifier       orb identifier.
     * @param orb              orb class.
     * @param category         orb category.
     * @param defaultsProvider default properties.
     */
    public boolean registerOrb(String identifier, Class<? extends OrbBase> orb, OrbCategory category, OrbDefaultsProvider defaultsProvider) {
        if (identifier.length() > 20) {
            throw new IllegalArgumentException("Identifier cannot be bigger than 20");
        }
        if (defaultsProvider.getDefaultActivityDelay() < 1) {
            throw new IllegalArgumentException("defaultActivityDelay cannot be lower than 1 tick");
        }
        // check if the identifier is in use
        if (orbTypeRegistry.values().stream().anyMatch(reg -> reg.isRegistered(identifier))){
            throw new IllegalStateException("Identifier already in use!");
        }
        OrbCategoryRegistry subRegistry = getCategoryRegistry(category);
        return subRegistry != null && subRegistry.addOrb(identifier, orb, defaultsProvider);
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
    public void despawnOrb(OrbBase orb) {
        OrbCategory orbCategory = getActiveOrbCategory(orb.getOrbId());
        if (orbCategory != null) {
            OrbCategoryRegistry subRegistry = getCategoryRegistry(orbCategory);
            subRegistry.removeActiveOrb(orb.getOrbId());
        }
        orb.getOrbEntity().destroy();
    }

    /**
     * Despawn an orb.
     */
    public void despawnOrb(OrbBase orb, OrbCategory category) {
        int orbId = orb.getOrbId();
        if (category != null) {
            OrbCategoryRegistry subRegistry = getCategoryRegistry(category);
            subRegistry.removeActiveOrb(orbId);
        }
        Bukkit.getScheduler().runTaskAsynchronously(HandyOrbsPlugin.getInstance(),
                () -> OrbRepository.getInstance().markOrbAsRemovedFromGround(orbId));

        orb.getOrbEntity().destroy();
    }


    @SuppressWarnings("unused")
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

    @SuppressWarnings("unused")
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
                throw new RuntimeException("Not implemented yet!");
        }
        return region;
    }

    public void handleOrbPlace(PlayerInteractEvent event) {
        final Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) return;
        final ItemStack item = event.getItem();
        final Player player = event.getPlayer();
        final String id = HandyOrbsCore.getInstance().getItemStackSupport().getTag(item, HandyOrbsPlugin.ORB_ID_TAG);
        final String type = HandyOrbsCore.getInstance().getItemStackSupport().getTag(item, HandyOrbsPlugin.ORB_TYPE_TAG);
        final String category = HandyOrbsCore.getInstance().getItemStackSupport().getTag(item, HandyOrbsPlugin.ORB_CATEGORY_TAG);
        if (category == null) {
            return;
        }
        final OrbCategory orbCat = OrbCategory.valueOf(category.toUpperCase());

        OrbCategoryRegistry categoryRegistry = getCategoryRegistry(orbCat);
        OrbDefaultsProvider defaults = categoryRegistry.getDefaultProviders().get(type);

        // take item
        HandyOrbsCore.getInstance().getItemStackSupport().minusAmount(player, item, 1);

        // go async
        Bukkit.getScheduler().runTaskAsynchronously(HandyOrbsPlugin.getInstance(), () -> {
            OrbEntity model = id == null || id.isEmpty() ? null : OrbRepository.getInstance().getOrbById(Integer.parseInt(id));
            if (model == null) {
                // get back in sync
                Bukkit.getScheduler().runTask(HandyOrbsPlugin.getInstance(), () -> {
                    OrbBase orb = OrbRegistry.getInstance().spawnOrb(type, orbCat,
                            clickedBlock.getLocation().clone().add(0, 3, 0),
                            defaults.getDefaultRegionString(), defaults.getDefaultActivityDelay()
                    );
                    if (orb == null) {
                        throw new IllegalStateException("This should not be null");
                    }
                    if (orb instanceof Ownable) {
                        ((Ownable) orb).setOwner(player.getUniqueId());
                    }
                    orb.getOrbEntity().setDisplayName(defaults.getDefaultDisplayName().replace("{player}", player.getDisplayName()));
                    orb.getOrbEntity().setRightClickListener(OrbRightClickHandler.getInstance().getDefaultRightClickListener(orb));
                    orb.getOrbEntity().setIcon(defaults.getDefaultIcon());

                    //go async and save to db
                    Bukkit.getScheduler().runTaskAsynchronously(HandyOrbsPlugin.getInstance(), () -> {
                        OrbEntity orbEntity = OrbRepository.getInstance().saveUpdate(orb, OrbCategory.FARMING);
                        if (orbEntity != null) {
                            //get in sync and set the orb id and add it to registry
                            Bukkit.getScheduler().runTask(HandyOrbsPlugin.getInstance(), () -> {
                                orb.setOrbId(orbEntity.getOrbId());
                                OrbCategoryRegistry registry = OrbRegistry.getInstance().getCategoryRegistry(orbCat);
                                registry.addActiveOrb(orb.getOrbId(), orb);
                            });
                        }
                    });
                });
            } else {
                // get in sync and spawn orb
                Bukkit.getScheduler().runTask(HandyOrbsPlugin.getInstance(), () -> {
                    OrbBase orb = OrbRegistry.getInstance().spawnOrb(type, orbCat,
                            clickedBlock.getLocation().clone().add(0, 3, 0),
                            model.getRegion(), model.getActivityDelay());
                    if (orb == null) {
                        throw new IllegalStateException("This should not be null!");
                    }
                    orb.getOrbEntity().setDisplayName(model.getDisplayName());
                    orb.getOrbEntity().setCustomNameVisible(model.isNameStatus());
                    if (orb instanceof TimedOrb) {
                        ((TimedOrb) orb).setActivityDelay(model.getActivityDelay());
                    }
                    if (orb instanceof Ownable) {
                        ((Ownable) orb).setOwner(player.getUniqueId());
                    }

                    orb.getOrbEntity().setRightClickListener(OrbRightClickHandler.getInstance().getDefaultRightClickListener(orb));
                    orb.getOrbEntity().setIcon(defaults.getDefaultIcon());

                    categoryRegistry.addActiveOrb(orb.getOrbId(), orb);

                    // go in async and save new placed location to db and eventual new owner etc.
                    Bukkit.getScheduler().runTaskAsynchronously(HandyOrbsPlugin.getInstance(), () ->
                            OrbRepository.getInstance().saveUpdate(orb, OrbCategory.FARMING));
                });
            }
        });
    }

    /**
     * Get a list of orbs.
     */
    public List<String> getOrbTypes() {
        List<String> types = new ArrayList<>();
        orbTypeRegistry.forEach((category, registry) -> types.addAll(registry.getOrbTypes()));
        return types;
    }

    @Nullable
    public OrbCategory getCategoryByOrbType(String type) {
        for (var entry : orbTypeRegistry.entrySet()) {
            if (entry.getValue().getOrb(type) != null) {
                return entry.getKey();
            }
        }
        return null;
    }

    public OrbCategoryRegistry getCategoryRegistryByOrbType(String type) {
        for (var entry : orbTypeRegistry.entrySet()) {
            if (entry.getValue().getOrb(type) != null) {
                return entry.getValue();
            }
        }
        return null;
    }

    public ItemStack getOrbItem(String string, Player player) {

        var category = getCategoryByOrbType(string);
        if (category == null) {
            throw new IllegalStateException("This should not happen");
        }

        var categoryRegistry = getCategoryRegistryByOrbType(string);
        if (categoryRegistry == null) {
            throw new IllegalStateException("This should not happen");
        }

        var defaults = categoryRegistry.getDefaultProviders().get(string);
        if (defaults == null) {
            throw new IllegalStateException("This should not happen");
        }

        var orbItem = defaults.getDefaultIcon();
        var im = orbItem.getItemMeta();
        if (im != null) {
            im.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                    defaults.getDefaultDisplayName().replace("{player}", player.getDisplayName())
            ));
            orbItem.setItemMeta(im);
        }
        orbItem = HandyOrbsCore.getInstance().getItemStackSupport().addTag(orbItem, HandyOrbsPlugin.ORB_TYPE_TAG, string);
        orbItem = HandyOrbsCore.getInstance().getItemStackSupport().addTag(orbItem, HandyOrbsPlugin.ORB_CATEGORY_TAG, category.toString().toLowerCase());
        orbItem = HandyOrbsCore.getInstance().getItemStackSupport().addTag(orbItem, HandyOrbsPlugin.ORB_CHECKER_TAG, "yes");
        return orbItem;
    }
}
