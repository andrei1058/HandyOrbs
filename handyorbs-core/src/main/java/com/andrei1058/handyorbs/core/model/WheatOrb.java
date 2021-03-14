package com.andrei1058.handyorbs.core.model;

import com.andrei1058.handyorbs.core.HandyOrbsCore;
import com.andrei1058.handyorbs.core.region.IRegion;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class WheatOrb extends GenericFarmOrb {

    private static ItemStack cachedIcon = null;

    public WheatOrb(Location location, IRegion region, Integer delay) {
        super(location, region, Material.WHEAT, HandyOrbsCore.getInstance().getMaterialSupport().getSoil(), delay);
        getOrbEntity().setIcon(getCachedIcon() == null ? new ItemStack(Material.HAY_BLOCK) : getCachedIcon());
    }

    @Nullable
    public static ItemStack getCachedIcon() {
        return cachedIcon;
    }

    public static void setCachedIcon(ItemStack cachedIcon) {
        WheatOrb.cachedIcon = cachedIcon;
    }
}
