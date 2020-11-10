package com.andrei1058.handyorbs;

import com.andrei1058.handyorbs.core.HandyOrbsCore;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.plugin.ApiVersion;
import org.bukkit.plugin.java.annotation.plugin.Description;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.bukkit.plugin.java.annotation.plugin.author.Author;

@Plugin(name = "HandyOrbs", version = "3.0.0")
@Description("Orbs that come in handy.")
@ApiVersion(value = ApiVersion.Target.v1_13)
@Author("andrei1058")
public class HandyOrbsPlugin extends JavaPlugin {

    private static HandyOrbsCore handyOrbsCore;

    @Override
    public void onEnable() {
        handyOrbsCore = new HandyOrbsCore(this);
    }

    /**
     * Get core.
     */
    public static HandyOrbsCore getHandyOrbsCore() {
        return handyOrbsCore;
    }
}
