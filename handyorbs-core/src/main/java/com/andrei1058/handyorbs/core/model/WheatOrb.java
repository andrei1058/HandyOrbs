package com.andrei1058.handyorbs.core.model;

import com.andrei1058.handyorbs.core.HandyOrbsCore;
import com.andrei1058.handyorbs.core.region.IRegion;
import org.bukkit.Location;
import org.bukkit.Material;

public class WheatOrb extends GenericFarmOrb {

    public WheatOrb(Location location, IRegion region, Integer delay) {
        super(location, region, Material.WHEAT, HandyOrbsCore.getInstance().getMaterialSupport().getSoil(), delay);
    }

}
