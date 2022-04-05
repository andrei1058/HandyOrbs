package com.andrei1058.handyorbs.core.version;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

interface WrappedFactory {

    OrbEntity spawnOrbEntity(Location location, ItemStack head);
}
