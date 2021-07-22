package com.andrei1058.handyorbs.api;

import org.bukkit.inventory.ItemStack;

public interface OrbDefaultsProvider {

    int getDefaultActivityDelay();

    String getDefaultRegionString();

    String getDefaultDisplayName();

    ItemStack getDefaultIcon();
}
