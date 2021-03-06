package com.andrei1058.handyorbs.config.types;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.properties.StringProperty;
import com.andrei1058.handyorbs.HandyOrbsPlugin;
import com.andrei1058.handyorbs.api.OrbCategory;
import com.andrei1058.handyorbs.core.HandyOrbsCore;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

public class WheatOrbConfig implements SettingsHolder {

    private static ItemStack cachedItemStack = null;

    private WheatOrbConfig() {
    }

    @Comment({"This is the item users receive in their inventories and it is used as well as floating item for the orb.",
            "Options:",
            "skin:codeHere - you can get skins from minecraft-heads.com and paste here the 'Minecraft-URL' after 'skin:'",
            "material:name,data - where name is the material name and data is the material data for 1.12-."})
    public static final Property<String> ITEM_STACK = new StringProperty("item-stack", "skin:4e3ca5b390d1e5f297283257ce90ac6f8783d786ecaee095b49cc6b944d72d");

    @Override
    public void registerComments(CommentsConfiguration conf) {
        conf.setComment("", "HandyOrbs by andrei1058", "Discord: https://discord.gg/XdJfN2X", "", "Wheat orb customisation.");
    }

    public static SettingsManager getConfig() {
        File templateFile = new File(HandyOrbsPlugin.getInstance().getDataFolder(), "types" + File.separator + "wheat.yml");
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
        return SettingsManagerBuilder.withYamlFile(templateFile).configurationData(WheatOrbConfig.class).useDefaultMigrationService().create();
    }

    public static ItemStack getCachedItemStack() {
        if (cachedItemStack == null) {
            String option = getConfig().getProperty(ITEM_STACK);
            if (option.startsWith("skin:")) {
                cachedItemStack = HandyOrbsCore.getInstance().getItemStackSupport().applySkinTextureOnHead(option.replaceFirst("skin:", "").trim(), null);
            } else if (option.startsWith("material:")) {
                String[] options = option.replaceFirst("material:", "").trim().split(",");
                byte data = options.length > 1 ? Byte.valueOf(options[1]) : 0;
                cachedItemStack = HandyOrbsCore.getInstance().getItemStackSupport().createItem(options[0], 1, data);
            } else {
                cachedItemStack = new ItemStack(Material.BEDROCK);
            }
        }
        return cachedItemStack;
    }

    /**
     * Get give item. Used when you summon a new orb or take it back.
     *
     * @param id id, if a orb was de-spawned.
     */
    public static ItemStack getGiveItem(@Nullable String id){
        ItemStack orbItem = getCachedItemStack();
        if (id != null) {
            orbItem = HandyOrbsCore.getInstance().getItemStackSupport().addTag(orbItem, HandyOrbsPlugin.ORB_ID_TAG, id);
        }
        orbItem = HandyOrbsCore.getInstance().getItemStackSupport().addTag(orbItem, HandyOrbsPlugin.ORB_TYPE_TAG, "wheat");
        orbItem = HandyOrbsCore.getInstance().getItemStackSupport().addTag(orbItem, HandyOrbsPlugin.ORB_CATEGORY_TAG, OrbCategory.FARMING.name());
        orbItem = HandyOrbsCore.getInstance().getItemStackSupport().addTag(orbItem, HandyOrbsPlugin.ORB_CHECKER_TAG, "yes");
        return orbItem;
    }
}
