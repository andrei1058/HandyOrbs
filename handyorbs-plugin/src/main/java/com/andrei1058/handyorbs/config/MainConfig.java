package com.andrei1058.handyorbs.config;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.BooleanProperty;
import ch.jalu.configme.properties.IntegerProperty;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.properties.StringProperty;
import com.andrei1058.handyorbs.HandyOrbsPlugin;
import org.bukkit.Material;

import java.io.File;
import java.io.IOException;

public class MainConfig implements SettingsHolder {

    @Comment({"Default/ fallBack server language."})
    public static final Property<String> LOCALE_FALLBACK = new StringProperty("locale.default-locale", "en");
    @Comment("Languages folder path.")
    public static final Property<String> LOCALE_FOLDER = new StringProperty("locale.custom-path", "");
    @Comment("This will enable the /handyorbs lang command so players can choose their language.")
    public static final Property<Boolean> LOCALE_COMMAND = new BooleanProperty("locale.enable-command", true);
    @Comment("With this on true language preferences will be stored on db.")
    public static final Property<Boolean> LOCALE_SYNC_DB = new BooleanProperty("locale.sync-to-db", true);
    @Comment({"The display item on which you click to change the orb name :)"})
    public static final StringProperty ORB_GUI_CHANGE_NAME_MATERIAL = new StringProperty("self-orb-gui.change-name-material", Material.FEATHER.toString());
    @Comment({"The slot number where to put the item in the inventory."})
    public static final IntegerProperty ORB_GUI_CHANGE_NAME_SLOT = new IntegerProperty("self-orb-gui.change-name-slot", 0);
    @Comment({"The display item on which you click to toggle the orb name visibility."})
    public static final StringProperty ORB_GUI_TOGGLE_NAME_MATERIAL = new StringProperty("self-orb-gui.toggle-name-visibility-material", Material.SOUL_LANTERN.toString());
    @Comment({"The slot number where to put the item in the inventory."})
    public static final IntegerProperty ORB_GUI_TOGGLE_NAME_SLOT = new IntegerProperty("self-orb-gui.toggle-name-visibility-slot", 1);
    @Comment({"Orb name char limit"})
    public static final IntegerProperty ORB_NAME_CHAR_LIMIT = new IntegerProperty("orb-name-char-limit", 20);

    @Comment({
            "For SQLite use: jdbc:sqlite:./plugins/HandyOrbsReborn/local_orbs.db",
            "For MySQL use: jdbc:mysql://HOST:3306/DATABASE",
    })
    public static final StringProperty DATABASE_URL = new StringProperty("database.connection-string",
            "jdbc:sqlite:./plugins/" + HandyOrbsPlugin.getInstance().getName() + "/local_orbs.db");
    public static final StringProperty DATABASE_USERNAME = new StringProperty("database.username", "root");
    public static final StringProperty DATABASE_PASSWORD = new StringProperty("database.password", "bread");

    @Override
    public void registerComments(CommentsConfiguration conf) {
        conf.setComment("self-orb-gui", " ", "Configuration for the GUI when you right click your orb.");
        conf.setComment("locale", " ", "Language configuration.");
        conf.setComment("database",  " ", "Database configuration.");
    }

    public static SettingsManager getConfig() {
        File templateFile = new File(HandyOrbsPlugin.getInstance().getDataFolder(), "config.yml");
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
