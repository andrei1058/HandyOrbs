package com.andrei1058.handyorbs.hook;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface PAPIHook {

    /**
     * Translate placeholders if PAPI is loaded.
     * Otherwise will return the original string.
     */
    @NotNull
    String parsePlaceholders(@NotNull Player player, @NotNull String message);

}
