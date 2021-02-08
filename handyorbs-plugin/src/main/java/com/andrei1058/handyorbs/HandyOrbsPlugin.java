package com.andrei1058.handyorbs;

import com.andrei1058.handyorbs.api.HandyOrbs;
import com.andrei1058.handyorbs.api.OrbCategory;
import com.andrei1058.handyorbs.core.HandyOrbsCore;
import com.andrei1058.handyorbs.core.OrbBase;
import com.andrei1058.handyorbs.database.OrbRepository;
import com.andrei1058.handyorbs.listener.ChunkListener;
import com.andrei1058.handyorbs.registry.OrbRegistry;
import com.andrei1058.handyorbs.test.Listener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.plugin.ApiVersion;
import org.bukkit.plugin.java.annotation.plugin.Description;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.bukkit.plugin.java.annotation.plugin.author.Author;

@Plugin(name = "HandyOrbsReborn", version = "3.0.0")
@Description("Orbs that come in handy.")
@ApiVersion(value = ApiVersion.Target.v1_13)
@Author("andrei1058")
public class HandyOrbsPlugin extends JavaPlugin implements HandyOrbs {

    private static HandyOrbsPlugin instance;

    @Override
    public void onEnable() {
        instance = this;
        if (!HandyOrbsCore.init(this)){
            getLogger().severe("Server version not supported!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        //todo make url configurable
        if (!OrbRepository.init("jdbc:sqlite:sample.db")){
            getLogger().severe("Could not connect to database!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        OrbRegistry.init();

        // Register listeners
        Bukkit.getPluginManager().registerEvents(new ChunkListener(), this);
        //todo test listener
        Bukkit.getPluginManager().registerEvents(new Listener(), this);
    }

    public static HandyOrbsPlugin getInstance() {
        return instance;
    }

    public boolean registerOrb(String identifier, Class<? extends OrbBase> orb, OrbCategory category) {
        return OrbRegistry.getInstance().registerOrb(identifier, orb, category);
    }

    @Override
    public OrbBase spawnOrb(String identifier, OrbCategory category, Location location, int radius) {
        return OrbRegistry.getInstance().spawnOrb(identifier, category, location, radius);
    }
}
