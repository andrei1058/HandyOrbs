package com.andrei1058.handyorbs.core.model;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.LinkedList;

public interface Farmable {

    LinkedList<Location> getSoil();

    Material getCropMaterial();

    Material getSoilMaterial();

    default Material getUpperMaterial() {
        return Material.AIR;
    }

    /**
     * Check if can plant on given soil block.
     * This will check if it is soil as well.
     * Checks the upper block and the region as well.
     *
     * @return null if false otherwise seeds Block (upper block).
     */
    Block canPlant(Block soil);

    void addSoil(Location location);

    void removeSoil(Location location);
}
