package com.andrei1058.handyorbs.core.model;

/**
 * A timed orb has a countdown used to plant crops etc.
 */
public interface TimedOrb {

    /**
     * Get orb activity delay.
     * Like for planting crops etc.
     * @return in server ticks
     */
    int getActivityDelay();

    /**
     * Set orb activity delay.
     * Ex: time between planting crops.
     * @param ticks time in server ticks.
     */
    void setActivityDelay(int ticks);

    /**
     * Get current countdown until next activity.
     * @return ticks.
     */
    int getCountdown();
}
