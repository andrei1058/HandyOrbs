package com.andrei1058.handyorbs.core.region;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.LinkedList;
import java.util.List;

public class Cuboid implements IRegion {

    private final int minX, maxX;
    private final int minY, maxY;
    private final int minZ, maxZ;

    public Cuboid(int radius, Location center) {
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

    @Override
    public boolean isInRegion(int x, int y, int z) {
        return (x <= maxX && x >= minX) && (y <= maxY && y >= minY) && (z <= maxZ && z >= minZ);
    }

    @Override
    public String toExport() {
        return "internal;cuboid;" + ((maxX - minX) / 2);
    }

    @Override
    public List<Location> getSoilBlocks(World world, Material material) {
        List<Location> blocks = new LinkedList<>();
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    if (block.getType() == material && block.getRelative(BlockFace.UP).getType() == Material.AIR) {
                        blocks.add(block.getRelative(BlockFace.UP).getLocation());
                    }
                }
            }
        }
        return blocks;
    }
}
