package com.andrei1058.handyorbs.core.version;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public interface OrbEntity {

    /**
     * Set display name.
     */
    void setDisplayName(String string);

    String getName();

    /**
     * Set armor stand helmet.
     */
    void setIcon(ItemStack itemStack);

    /**
     * Remove entity.
     */
    void destroy();

    /**
     * Get Y loc.
     */
    double getLocY();

    double getLocX();

    double getLocZ();


    int getChunkX();

    int getChunkZ();

    /**
     * Tick float animation.
     */
    void floatY(boolean animationUp);

    /**
     * Set right click listener.
     */
    void setRightClickListener(@Nullable Function<Player, Void> rightClickListener);

    boolean getCustomNameVisible();

    void setCustomNameVisible(boolean toggle);

    // testing

    default void tickAnimation() {
        if (isAnimationUp()) {
            if (getLocY() >= getMaxY()) {
                setAnimationUp(false);
            }
        } else {
            if (getLocY() <= getMinY()) {
                setAnimationUp(true);
            }
        }
        //getArmorStand().teleport(location);
        floatY(isAnimationUp());
    }

    /**
     * @return true if floating is going up.
     */
    boolean isAnimationUp();

    double getMaxY();

    double getMinY();

    void setAnimationUp(boolean toggle);

    OrbActivity getOrbActivity();

    void setOrbActivity(OrbActivity orbActivity);
}
