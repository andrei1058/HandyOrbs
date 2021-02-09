package com.andrei1058.handyorbs.core.model;

import com.andrei1058.handyorbs.core.HandyOrbsCore;
import com.andrei1058.handyorbs.core.OrbBase;
import com.andrei1058.handyorbs.core.region.IRegion;
import org.bukkit.*;

import java.util.LinkedList;
import java.util.Random;
import java.util.UUID;

public abstract class GenericFarmOrb extends OrbBase implements Ownable {

    private static final Random random = new Random();

    private UUID owner;
    private final Material cropMaterial;

    private final Material groundMaterial;
    private final LinkedList<Location> soil = new LinkedList<>();

    public GenericFarmOrb(final Location location, IRegion region, Material cropMaterial, Material groundMaterial, final int delay) {
        super(location, region, delay);
        this.cropMaterial = cropMaterial;
        this.groundMaterial = groundMaterial;
        prepareSoil(location.getWorld(), region);
        getOrbEntity().setOrbActivity(() -> {
            if (soil.isEmpty()) return;
            if (getOrbEntity().isHyperActivity()) {
                // particles in region
                // grow all blocks
                for (Location loc : soil) {
                    loc.getBlock().setType(getCropMaterial());
                }
                //todo daca e server orb planteaza crescut
                // check server orb cu owner == null
            } else {
                // grow block
                int entry = random.nextInt(soil.size());
                Location target = soil.remove(entry);
                particlePath(location.getBlockX(), location.getBlockY() + 0.7, location.getBlockZ(), target, 0, true);
            }
        });
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public UUID getOwner() {
        return owner;
    }

    public Material getCropMaterial() {
        return cropMaterial;
    }

    private void prepareSoil(World world, IRegion region) {
        soil.addAll(region.getSoilBlocks(world, groundMaterial));
    }

    public void particlePath(double startX, double startY, double startZ, Location end, float procent, boolean instantGrowth) {
        if (Math.floor(procent) == 1) {
            end.getBlock().setType(cropMaterial);
            if (instantGrowth) {
                HandyOrbsCore.getInstance().getBlockSupport().setBlockData(end.getBlock(), (byte) 7);
            }
            return;
        }
        float locX = (float) (startX + (end.getBlockX() - startX) * procent);
        float locY = (float) (startY + (float) (end.getY() - startY) * procent);
        float locZ = (float) (startZ + (end.getBlockZ() - startZ) * procent);
        Bukkit.getScheduler().runTaskLater(HandyOrbsCore.getInstance().getOwner(), () -> {
            HandyOrbsCore.getInstance().getParticleSupport().spawnParticle(end.getWorld(), "FIREWORKS_SPARK",
                    locX + 0.5f, locY, locZ + 0.5f, 0, 0, 0, 0, 1);
            particlePath(startX, startY, startZ, end, procent + 0.05f, instantGrowth);
        }, 1);
    }
}
