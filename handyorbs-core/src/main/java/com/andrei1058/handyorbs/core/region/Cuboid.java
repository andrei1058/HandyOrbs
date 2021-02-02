package com.andrei1058.handyorbs.core.region;

import org.bukkit.Location;

public class Cuboid implements IRegion{

    private final int minX, maxX;
    private final int minY, maxY;
    private final int minZ, maxZ;


    public Cuboid(int radius, Location center){
        Location l1 = center.clone().subtract(radius, radius, radius);
        Location l2 = center.clone().add(radius, radius, radius);

        minX = Math.min(l1.getBlockX(), l2.getBlockX());
        maxX = Math.max(l1.getBlockX(), l2.getBlockX());

        minY = Math.min(l1.getBlockY(), l2.getBlockY());
        maxY = Math.max(l1.getBlockY(), l2.getBlockY());

        minZ = Math.min(l1.getBlockZ(), l2.getBlockZ());
        maxZ = Math.max(l1.getBlockZ(), l2.getBlockZ());

    }

    @Override
    public boolean isInRegion(Location l) {
        return (l.getBlockX() <= maxX && l.getBlockX() >= minX) && (l.getY() <= maxY && l.getY() >= minY) && (l.getBlockZ() <= maxZ && l.getBlockZ() >= minZ);
    }
}
