package com.andrei1058.handyorbs;

import com.andrei1058.handyorbs.api.HandyOrbs;
import com.andrei1058.handyorbs.api.OrbCategory;
import com.andrei1058.handyorbs.command.HandyOrbsCommand;
import com.andrei1058.handyorbs.config.types.WheatOrbConfig;
import com.andrei1058.handyorbs.core.HandyOrbsCore;
import com.andrei1058.handyorbs.core.OrbBase;
import com.andrei1058.handyorbs.database.OrbRepository;
import com.andrei1058.handyorbs.gui.GUIManager;
import com.andrei1058.handyorbs.listener.ChunkListener;
import com.andrei1058.handyorbs.listener.OrbListener;
import com.andrei1058.handyorbs.registry.OrbRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.dependency.SoftDependency;
import org.bukkit.plugin.java.annotation.dependency.SoftDependsOn;
import org.bukkit.plugin.java.annotation.permission.Permission;
import org.bukkit.plugin.java.annotation.permission.Permissions;
import org.bukkit.plugin.java.annotation.plugin.ApiVersion;
import org.bukkit.plugin.java.annotation.plugin.Description;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.bukkit.plugin.java.annotation.plugin.author.Author;

@Plugin(name = "HandyOrbsReborn", version = "3.0.0")
@Description("Orbs that come in handy.")
@ApiVersion(value = ApiVersion.Target.v1_13)
@Author("andrei1058")
@SoftDependsOn(value = {@SoftDependency(value = "WorldGuard")})
@Permissions(value = {@Permission(desc = "Allow orb get command.", name = "handyorbs.get")})
public class HandyOrbsPlugin extends JavaPlugin implements HandyOrbs {

    private static HandyOrbsPlugin instance;
    public static final String ORB_ID_TAG = "handyorbsid";
    public static final String ORB_CHECKER_TAG = "handyorbsorb";
    public static final String ORB_TYPE_TAG = "handyorbstype";
    public static final String ORB_CATEGORY_TAG = "handyorbcategory";

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
        Bukkit.getPluginManager().registerEvents(new OrbListener(), this);

        // Register commands
        HandyOrbsCommand.init();

        // Initialize GUIs
        GUIManager.init();

        // Calling this will create the file if it is missing
        WheatOrbConfig.getConfig();
        // Calling this will initialize orb icon
        WheatOrbConfig.getCachedItemStack();
    }

    public static HandyOrbsPlugin getInstance() {
        return instance;
    }

    public boolean registerOrb(String identifier, Class<? extends OrbBase> orb, OrbCategory category) {
        return OrbRegistry.getInstance().registerOrb(identifier, orb, category);
    }

    @Override
    public OrbBase spawnOrb(String identifier, OrbCategory category, Location location, String radius, int delay) {
        return OrbRegistry.getInstance().spawnOrb(identifier, category, location, radius, delay);
    }

    public static void log(String msg){
        Bukkit.getLogger().info(msg);
    }
}
