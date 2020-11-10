package com.andrei1058.handyorbs.core;

import org.bukkit.Location;

public class OrbBase {

    private Location location;

    protected OrbBase(Location location){
        this.location = location;
    }

    public boolean load(){
        return false;
    }

    public void unLoad(){

    }

    public void changeLocation(Location newLocation){
        this.location = newLocation;
        // todo teleport orb
    }

    protected void tickAnimation(){

    }

}
