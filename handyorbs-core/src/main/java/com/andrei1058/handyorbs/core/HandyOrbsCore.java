package com.andrei1058.handyorbs.core;

import com.andrei1058.handyorbs.core.version.OrbEntityFactory;
import com.andrei1058.spigot.versionsupport.BlockSupport;
import com.andrei1058.spigot.versionsupport.MaterialSupport;
import com.andrei1058.spigot.versionsupport.ParticleSupport;
import org.bukkit.plugin.Plugin;

public class HandyOrbsCore {

    private final Plugin owner;
    private static HandyOrbsCore instance;
    private final MaterialSupport materialSupport;
    private final BlockSupport blockSupport;
    private final ParticleSupport particleSupport;

    private HandyOrbsCore(Plugin plugin) {
        this.owner = plugin;
        materialSupport = MaterialSupport.SupportBuilder.load();
        blockSupport = BlockSupport.SupportBuilder.load();
        particleSupport = ParticleSupport.SupportBuilder.load();
    }

    /**
     * Initialize orbs core.
     *
     * @param plugin owner. Used for registering tasks etc.
     * @return false if server version is not supported.
     */
    public static boolean init(Plugin plugin) {
        if (instance == null) {
            if (!OrbEntityFactory.init()){
                return false;
            }
            instance = new HandyOrbsCore(plugin);
        }
        return true;
    }

    /**
     * Get owning plugin.
     */
    public Plugin getOwner() {
        return owner;
    }

    public static HandyOrbsCore getInstance() {
        return instance;
    }

    public MaterialSupport getMaterialSupport() {
        return materialSupport;
    }

    public BlockSupport getBlockSupport() {
        return blockSupport;
    }

    public ParticleSupport getParticleSupport() {
        return particleSupport;
    }

}
