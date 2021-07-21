package com.andrei1058.handyorbs.hook.papi;

import com.andrei1058.handyorbs.hook.PAPIHook;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PAPISupport implements PAPIHook {

    @Override
    public @NotNull
    String parsePlaceholders(@NotNull Player player, @NotNull String message) {
        return PlaceholderAPI.setPlaceholders(player, message);
    }

}
