package com.andrei1058.handyorbs.config;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import ch.jalu.configme.properties.IntegerProperty;
import ch.jalu.configme.properties.StringProperty;
import com.andrei1058.handyorbs.HandyOrbsPlugin;
import org.bukkit.Material;

import java.io.File;
import java.io.IOException;

public class MainConfig implements SettingsHolder {

    @Comment({"The display item on which you click to change the orb name :)"})
    public static final StringProperty CHANGE_NAME_MATERIAL = new StringProperty("change-name-material", Material.FEATHER.toString());
    @Comment({"The slot number where to put the item in the inventory."})
    public static final IntegerProperty CHANGE_NAME_SLOT = new IntegerProperty("change-name-slot", 0);


    public static SettingsManager getConfig() {
        File templateFile = new File(HandyOrbsPlugin.getInstance().getDataFolder(), "orb_gui.yml");
        if (!templateFile.getParentFile().exists()) {
            if (!templateFile.getParentFile().mkdirs()) {
                HandyOrbsPlugin.getInstance().getLogger().warning("Could not create: " + templateFile.getPath());
            }
        }
        if (!templateFile.exists()) {
            try {
                //noinspection ResultOfMethodCallIgnored
                templateFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return SettingsManagerBuilder.withYamlFile(templateFile).configurationData(MainConfig.class).useDefaultMigrationService().create();
    }
}
