package com.andrei1058.handyorbs.api;

import com.andrei1058.handyorbs.api.locale.LocaleManager;
import com.andrei1058.handyorbs.core.OrbBase;
import org.bukkit.Location;

public interface HandyOrbs {

    /**
     * Register a new orb.
     *
     * @param identifier           unique identifier.
     * @param orb                  orb class.
     * @param category             orb category.
     * @param defaultsProvider default properties.
     */
    boolean registerOrb(String identifier, Class<? extends OrbBase> orb, OrbCategory category, OrbDefaultsProvider defaultsProvider);

    /**
     * Spawn a new orb.
     */
    OrbBase spawnOrb(String identifier, OrbCategory category, Location location, String radius, int delay);

    /**
     * Get language manager.
     */
    LocaleManager getLocaleManager();
}
