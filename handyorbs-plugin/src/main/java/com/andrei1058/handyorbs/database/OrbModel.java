package com.andrei1058.handyorbs.database;

import java.util.UUID;

public class OrbModel {

    private final int orbId;
    private final int locX;
    private final int locY;
    private final int locZ;
    private final String world;
    private final String category;
    private final String type;
    private final UUID owner;

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

    public String getWorld() {
        return world;
    }

    public String getCategory() {
        return category;
    }

    public String getType() {
        return type;
    }

    public UUID getOwner() {
        return owner;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isNameStatus() {
        return nameStatus;
    }

    private final String displayName;
    private final boolean nameStatus;

    public OrbModel(int orbId, int locX, int locY, int locZ, String world, String category, String type, UUID owner, String displayName, int nameStatus) {
        this.orbId = orbId;
        this.locX = locX;
        this.locY = locY;
        this.locZ = locZ;
        this.world = world;
        this.category = category;
        this.type = type;
        this.owner = owner;
        this.displayName = displayName;
        this.nameStatus = nameStatus == 1;
    }
}
