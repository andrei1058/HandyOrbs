package com.andrei1058.handyorbs.core.version;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

interface WrappedFactory {

    @Nullable
    OrbEntity spawnOrbEntity(@NotNull Location location, @NotNull ItemStack head);
}
