package com.andrei1058.handyorbs.database;

import java.util.UUID;

import com.andrei1058.handyorbs.api.OrbCategory;
import com.andrei1058.handyorbs.core.OrbBase;
import com.andrei1058.handyorbs.core.model.Ownable;
import com.andrei1058.handyorbs.registry.OrbRegistry;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
@DatabaseTable(tableName = "orbs")
public class OrbEntity {

    @DatabaseField(generatedId = true, useGetSet = true)
    private int orbId;
    @DatabaseField(useGetSet = true)
    private int locX;
    @DatabaseField(useGetSet = true)
    private int locY;
    @DatabaseField(useGetSet = true)
    private int locZ;
    @DatabaseField(useGetSet = true)
    private String category;
    @DatabaseField(canBeNull = false, useGetSet = true)
    private String type;
    @DatabaseField(useGetSet = true)
    private UUID owner;
    @DatabaseField(canBeNull = false, useGetSet = true)
    private String displayName;
    @DatabaseField(canBeNull = false, useGetSet = true)
    private boolean nameStatus;
    @DatabaseField(useGetSet = true)
    private int chunkX;
    @DatabaseField(useGetSet = true)
    private int chunkZ;
    @DatabaseField(useGetSet = true)
    private String world;

    public OrbEntity(){
        // ORM Constructor
    }

    public OrbEntity(OrbBase orbBase) {
        this.locX = (int) orbBase.getOrbEntity().getLocX();
        this.locY = (int) orbBase.getOrbEntity().getLocY();
        this.locZ = (int) orbBase.getOrbEntity().getLocZ();
        this.world = orbBase.getWorld();


        OrbCategory orbCategory = OrbRegistry.getInstance().getActiveOrbCategory(orbBase.getOrbId());
        if (orbCategory == null) {
            //todo only for testing purposes, to remove
            orbCategory = OrbCategory.FARMING;
        }
        String orbIdentifier = OrbRegistry.getInstance().getActiveOrbIdentifier(orbBase);
        if (orbIdentifier == null) {
            //todo only for testing purposes, to remove
            orbIdentifier = "wheat";
        }
        this.category = orbCategory.name();
        this.type = orbIdentifier;
        this.owner = orbBase instanceof Ownable ? ((Ownable) orbBase).getOwner() : null;
        this.displayName = orbBase.getDisplayName();
        this.nameStatus = orbBase.getOrbEntity().getCustomNameVisible();
    }

    public int getOrbId() {
        return orbId;
    }

    public int getLocX() {
        return locX;
    }

    public int getLocY() {
        return locY;
    }

    public int getLocZ() {
        return locZ;
    }

    @Nullable
    public String getWorld() {
        return world;
    }

    public String getCategory() {
        return category;
    }

    public String getType() {
        return type;
    }

    @Nullable
    public UUID getOwner() {
        return owner;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isNameStatus() {
        return nameStatus;
    }

    public void setOrbId(int orbId) {
        this.orbId = orbId;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setLocX(int locX) {
        this.locX = locX;
    }

    public void setLocY(int locY) {
        this.locY = locY;
    }

    public void setLocZ(int locZ) {
        this.locZ = locZ;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setWorld(@Nullable String world) {
        this.world = world;
    }

    public void setOwner(@Nullable UUID owner) {
        this.owner = owner;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setNameStatus(boolean nameStatus) {
        this.nameStatus = nameStatus;
    }

    public int getChunkX() {
        return chunkX;
    }

    public int getChunkZ() {
        return chunkZ;
    }

    public void setChunkZ(int chunkZ) {
        this.chunkZ = chunkZ;
    }

    public void setChunkX(int chunkX) {
        this.chunkX = chunkX;
    }
}
