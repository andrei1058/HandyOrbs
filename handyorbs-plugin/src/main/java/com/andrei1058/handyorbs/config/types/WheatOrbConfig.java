package com.andrei1058.handyorbs.config.types;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.properties.StringProperty;

public class WheatOrbConfig implements SettingsHolder {

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
}
