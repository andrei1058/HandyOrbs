package com.andrei1058.handyorbs.gui;

import org.bukkit.entity.Player;

public class GUIManager {

    private static GUIManager instance;

    /**
     * Initialize GUI Handler.
     */
    public static void init() {
        if (instance == null) {
            instance = new GUIManager();
        }
    }

    public static GUIManager getInstance() {
        return instance;
    }

    private GUIManager() {

    }

    /**
     * Open orbs gui.
     * Use different params to show someone else's orbs.
     *
     * @param receiver is the GUI receiver.
     * @param owner    is the orbs owner.
     */
    public void openSelf(Player receiver, Player owner) {

    }
}
