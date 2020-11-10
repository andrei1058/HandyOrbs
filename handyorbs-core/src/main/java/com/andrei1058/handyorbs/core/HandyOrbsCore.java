package com.andrei1058.handyorbs.core;

import org.bukkit.plugin.Plugin;

public class HandyOrbsCore {

    private final Plugin owner;

    /**
     * Initialize orbs core.
     *
     * @param plugin owner. Used for registering tasks etc.
     */
    public HandyOrbsCore(Plugin plugin) {
        this.owner = plugin;
    }

    /**
     * Get owning plugin.
     */
    public Plugin getOwner() {
        return owner;
    }
}
