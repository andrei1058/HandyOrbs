package com.andrei1058.handyorbs.core.region;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.List;

public interface IRegion {

    boolean isInRegion(Location location);

    /**
     * Region data to be saved to db.
     */
    String toExport();

    List<Location> getSoilBlocks(World world, Material material);
}
