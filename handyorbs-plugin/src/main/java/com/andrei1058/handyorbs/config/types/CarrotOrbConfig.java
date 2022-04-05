package com.andrei1058.handyorbs.config.types;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.IntegerProperty;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.properties.StringProperty;
import com.andrei1058.handyorbs.HandyOrbsPlugin;
import com.andrei1058.handyorbs.api.OrbCategory;
import com.andrei1058.handyorbs.core.HandyOrbsCore;
import com.andrei1058.handyorbs.core.model.CarrotOrb;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

public class CarrotOrbConfig implements SettingsHolder {

    private CarrotOrbConfig() {
    }

    @Comment({"This is the item users receive in their inventories and it is used as well as floating item for the orb.",
            "Options:",
            "skin:codeHere - you can get skins from minecraft-heads.com and paste here the 'Minecraft-URL' after 'skin:'",
            "material:name,data - where name is the material name and data is the material data for 1.12-."})
    public static final Property<String> ITEM_STACK = new StringProperty("item-stack", "skin:4d3a6bd98ac1833c664c4909ff8d2dc62ce887bdcf3cc5b3848651ae5af6b");

    @Comment({"The orb name when it is placed by a player."})
    public static final Property<String> PLAYER_ORB_NAME = new StringProperty("orb-name-player", "&7{player}'s &6Carrot Orb");
    @Comment({"The orb name when it is converted to a server orb."})
    public static final Property<String> SERVER_ORB_NAME = new StringProperty("server-name-player", "&6&lCarrot Orb");
    @Comment({"Cuboid region size in blocks.", "Even if you change this, existing orbs will keep their size."})
    public static final Property<Integer> INTERNAL_REGION_SIZE = new IntegerProperty("internal-region-size", 10);

    public static final Property<Integer> PLANT_DELAY = new IntegerProperty("default-plant-interval", 10 * 20);

    @Override
    public void registerComments(CommentsConfiguration conf) {
        conf.setComment("", "HandyOrbs by andrei1058", "Discord: https://discord.gg/XdJfN2X", "", "Wheat orb customisation.");
    }

    public static SettingsManager getConfig() {
        File templateFile = new File(HandyOrbsPlugin.getInstance().getDataFolder(), "Types" + File.separator + "carrot.yml");
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
        return SettingsManagerBuilder.withYamlFile(templateFile).configurationData(CarrotOrbConfig.class).useDefaultMigrationService().create();
    }

    public static ItemStack getCachedItemStack() {
        if (CarrotOrb.getCachedIcon() == null) {
            String option = getConfig().getProperty(ITEM_STACK);
            if (option.startsWith("skin:")) {
                CarrotOrb.setCachedIcon(HandyOrbsCore.getInstance().getItemStackSupport().applySkinTextureOnHead(option.replaceFirst("skin:", "").trim(), null));
            } else if (option.startsWith("material:")) {
                String[] options = option.replaceFirst("material:", "").trim().split(",");
                byte data = options.length > 1 ? Byte.valueOf(options[1]) : 0;
                CarrotOrb.setCachedIcon(HandyOrbsCore.getInstance().getItemStackSupport().createItem(options[0], 1, data));
            }
        }
        return CarrotOrb.getCachedIcon() == null ? new ItemStack(Material.HAY_BLOCK) : CarrotOrb.getCachedIcon();
    }

    /**
     * Get give item. Used when you summon a new orb or take it back.
     *
     * @param id id, if a orb was de-spawned.
     */
    public static ItemStack getGiveItem(@Nullable String id) {
        ItemStack orbItem = getCachedItemStack();
        if (id != null) {
            orbItem = HandyOrbsCore.getInstance().getItemStackSupport().addTag(orbItem, HandyOrbsPlugin.ORB_ID_TAG, id);
        }
        orbItem = HandyOrbsCore.getInstance().getItemStackSupport().addTag(orbItem, HandyOrbsPlugin.ORB_TYPE_TAG, "carrot");
        orbItem = HandyOrbsCore.getInstance().getItemStackSupport().addTag(orbItem, HandyOrbsPlugin.ORB_CATEGORY_TAG, OrbCategory.FARMING.name());
        orbItem = HandyOrbsCore.getInstance().getItemStackSupport().addTag(orbItem, HandyOrbsPlugin.ORB_CHECKER_TAG, "yes");
        return orbItem;
    }
}
