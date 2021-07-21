package com.andrei1058.handyorbs.hook.papi;

import com.andrei1058.handyorbs.hook.PAPIHook;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PAPIFallback implements PAPIHook {

    @Override
    public @NotNull
    String parsePlaceholders(@NotNull Player player, @NotNull String message) {
        return message;
    }

}
