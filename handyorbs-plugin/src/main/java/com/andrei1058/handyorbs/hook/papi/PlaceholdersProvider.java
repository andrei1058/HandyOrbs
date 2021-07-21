package com.andrei1058.handyorbs.hook.papi;

import com.andrei1058.handyorbs.HandyOrbsPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlaceholdersProvider extends PlaceholderExpansion {

    private final String identifier;
    /**
     * Create a PAPI expansion for this plugin to provide awesome placeholders.
     *
     * @param identifier       first part of the placeholder. Like 'bw1058'.
     */
    public PlaceholdersProvider(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @NotNull
    String getIdentifier() {
        return identifier;
    }

    @Override
    public @NotNull
    String getAuthor() {
        return HandyOrbsPlugin.getInstance().getDescription().getAuthors().get(0);
    }

    @Override
    public @NotNull
    String getVersion() {
        return HandyOrbsPlugin.getInstance().getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        return parseCommonPlaceholders(player, identifier);
    }

    private String parseCommonPlaceholders(@Nullable Player player, String identifier) {

        //todo implement placeholders
        return null;
    }
}
