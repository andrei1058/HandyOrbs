package com.andrei1058.handyorbs.core;

import com.andrei1058.handyorbs.core.region.IRegion;
import com.andrei1058.handyorbs.core.version.OrbEntity;
import com.andrei1058.handyorbs.core.version.OrbEntityFactory;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

public abstract class OrbBase {

    private int orbId = -1;
    private String world;
    private IRegion region;
    private OrbEntity orbEntity;

    protected OrbBase(Location location, IRegion region, Integer delay) {
        this.world = location.getWorld() == null ? "null" : location.getWorld().getName();
        this.region = region;
        World world = Bukkit.getWorld(this.world);
        if (world == null) throw new IllegalStateException("World is not loaded!");
        orbEntity = OrbEntityFactory.getInstance().spawnOrbEntity(new Location(world, location.getX(), location.getY(), location.getZ()), new ItemStack(Material.GOLD_BLOCK));
        if (orbEntity == null) throw new IllegalStateException("Could not spawn orb entity!");
    }

    public void unLoad() {

    }

    public int getOrbId() {
        return orbId;
    }

    public OrbEntity getOrbEntity() {
        return orbEntity;
    }

    public String getWorld() {
        return world;
    }

    public String getDisplayName() {
        return orbEntity.getName();
    }

    public void setOrbId(int orbId) {
        this.orbId = orbId;
    }

    public IRegion getRegion() {
        return region;
    }
}
