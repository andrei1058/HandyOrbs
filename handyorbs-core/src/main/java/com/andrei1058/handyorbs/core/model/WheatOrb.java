package com.andrei1058.handyorbs.core.model;

import com.andrei1058.handyorbs.core.OrbBase;
import com.andrei1058.handyorbs.core.region.IRegion;
import org.bukkit.Location;

import java.util.UUID;

public class WheatOrb extends OrbBase implements Ownable {

    private UUID owner;

    public WheatOrb(Location location, IRegion region) {
        super(location, region);
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public UUID getOwner() {
        return owner;
    }
}
