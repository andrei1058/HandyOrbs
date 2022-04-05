package com.andrei1058.handyorbs.core.model;

import com.andrei1058.handyorbs.core.HandyOrbsCore;
import com.andrei1058.handyorbs.core.OrbBase;
import com.andrei1058.handyorbs.core.ParticleLoc;
import com.andrei1058.handyorbs.core.region.IRegion;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.LinkedList;
import java.util.Random;
import java.util.UUID;

public abstract class GenericFarmOrb extends OrbBase implements Ownable, Farmable {

    private static final Random random = new Random();

    private UUID owner;
    private final Material cropMaterial;

    private final Material groundMaterial;
    private final LinkedList<Location> soil = new LinkedList<>();

    private final LinkedList<ParticleLoc> particlePath = new LinkedList<>();
    private final int[] countdown = new int[]{100};
    private int activityDelay;
    private boolean instantGrowth = false;
    private boolean hyperActivity = false;
    private Location target = null;

    public GenericFarmOrb(final Location location, IRegion region, Material cropMaterial, Material groundMaterial, final int delay) {
        super(location, region, delay);
        this.cropMaterial = cropMaterial;
        this.groundMaterial = groundMaterial;
        this.activityDelay = delay;
        this.instantGrowth = true;
        prepareSoil(location.getWorld(), region);

        getOrbEntity().setOrbActivity(() -> {
            countdown[0]--;
            if (countdown[0] < 0) {
                if (particlePath.isEmpty()) {
                    countdown[0] = delay;
                    return;
                }
                ParticleLoc particle = particlePath.remove(0);
                HandyOrbsCore.getInstance().getParticleSupport().spawnParticle(target.getWorld(), "FIREWORKS_SPARK",
                        particle.getX(), particle.getY(), particle.getZ(), 0, 0, 0, 0, 1);
                if (particlePath.isEmpty() && target.getBlock().getType() == getUpperMaterial() && target.getBlock().getRelative(BlockFace.DOWN).getType() == getSoilMaterial()) {
                    countdown[0] = delay;
                    target.getBlock().setType(getCropMaterial());
                    if (isInstantGrowth()) {
                        HandyOrbsCore.getInstance().getBlockSupport().setBlockData(target.getBlock(), (byte) 7);
                    }
                    target = null;
                }
            } else if (countdown[0] == 0) {
                if (soil.isEmpty()) return;
                if (isHyperActivity()) {
                    // particles in region
                    // grow all blocks
                    for (Location loc : soil) {
                        loc.getBlock().setType(getCropMaterial());
                    }
                    if (getOwner() == null){
                        HandyOrbsCore.getInstance().getBlockSupport().setBlockData(target.getBlock(), (byte) 7);
                    }
                } else {
                    // grow block
                    int entry = random.nextInt(soil.size());
                    target = soil.remove(entry);
                    particlePath(location.getBlockX(), location.getBlockY() + 0.7, location.getBlockZ(), target, 0);
                    if (particlePath.isEmpty()) {
                        countdown[0] = delay;
                    }
                }
            }
        });
    }

    @Override
    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    @Override
    public UUID getOwner() {
        return owner;
    }

    @Override
    public Material getCropMaterial() {
        return cropMaterial;
    }

    private void prepareSoil(World world, IRegion region) {
        soil.addAll(region.getSoilBlocks(world, groundMaterial));
    }

    public void particlePath(double startX, double startY, double startZ, Location end, float procent) {
        if (Math.floor(procent) == 1) {
            return;
        }
        float locX = (float) (startX + (end.getBlockX() - startX) * procent);
        float locY = (float) (startY + (float) (end.getY() - startY) * procent);
        float locZ = (float) (startZ + (end.getBlockZ() - startZ) * procent);
        particlePath.add(new ParticleLoc(locX + 0.5f, locY + 1, locZ + 0.5f));
        particlePath(startX, startY, startZ, end, procent + 0.05f);
    }

    @Override
    public Material getSoilMaterial() {
        return groundMaterial;
    }

    @Override
    public LinkedList<Location> getSoil() {
        return soil;
    }

    public boolean isInstantGrowth() {
        return instantGrowth;
    }

    @SuppressWarnings("unused")
    public void setInstantGrowth(boolean instantGrowth) {
        this.instantGrowth = instantGrowth;
    }

    public boolean isHyperActivity() {
        return hyperActivity;
    }

    @SuppressWarnings("unused")
    public void setHyperActivity(boolean hyperActivity) {
        this.hyperActivity = hyperActivity;
    }

    @Override
    public Block canPlant(Block soil) {
        Block candidate = soil.getRelative(BlockFace.UP);
        if (soil.getType() == getSoilMaterial() && candidate.getType() == getUpperMaterial() && getRegion().isInRegion(candidate.getLocation())) {
            return candidate;
        }
        return null;
    }

    @Override
    public void addSoil(Location location) {
        soil.add(location);
    }

    @Override
    public void removeSoil(Location location) {
        soil.remove(location);
    }

    @Override
    public void setActivityDelay(int ticks) {
        if (ticks > 1){
            this.activityDelay = ticks;
        }
    }

    @Override
    public int getActivityDelay() {
        return activityDelay;
    }

    @Override
    public int getCountdown() {
        return countdown[0];
    }
}
