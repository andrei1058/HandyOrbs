package com.andrei1058.handyorbs.core;

import com.andrei1058.handyorbs.core.region.IRegion;
import com.andrei1058.handyorbs.core.version.OrbEntity;
import com.andrei1058.handyorbs.core.version.OrbEntityFactory;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public abstract class OrbBase {

    private int orbId = -1;
    private String world;
    private IRegion region;
    private OrbEntity orbEntity;

    protected OrbBase(Location location, IRegion region) {
        this.world = location.getWorld() == null ? "null" : location.getWorld().getName();
        this.region = region;
        World world = Bukkit.getWorld(this.world);
        if (world == null) throw new IllegalStateException("World is not loaded!");
        orbEntity = OrbEntityFactory.getInstance().spawnOrbEntity(new Location(world, location.getX(), location.getY(), location.getZ()), new ItemStack(Material.GOLD_BLOCK));
        if (orbEntity == null) throw new IllegalStateException("Could not spawn orb entity!");

        orbEntity.setDisplayName("&x&F&E&D&B&F&0My &x&C&B&A&F&C&0Nice &x&5&1&4&6&4&COrb");
        orbEntity.setRightClickListener((player -> {
            player.sendMessage("right click " + (player.isSneaking() ? "(shifting)" : ""));
            return null;
        }));
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

    public String getDisplayName(){
        return orbEntity.getName();
    }

    public void setOrbId(int orbId) {
        this.orbId = orbId;
    }
}
