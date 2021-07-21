package com.andrei1058.handyorbs.api.locale;

import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Collections;

public enum Message {

    ENABLE("enable-language", true),
    PREFIX("prefix", ""),
    NAME("name", "English"),
    TIME_ZONE("time-zone", "UTC+1"),
    DATE_FORMAT("date-format", "dd MMMM yyyy HH:mm"),
    DATE_NONE("date-never", "Never"),
    ORB_RENAME_GUI_NAME("orb-rename-gui-name", "&7Enter a new orb name:"),
    ORB_RENAME_ITEM_NAME("orb-rename-item-name", "&eChange orb name"),
    ORB_RENAME_ITEM_LORE("orb-rename-item-lore", Collections.emptyList()),
    ORB_SELF_GUI_NAME("orb-gui-name", "&7My {orbName}")


    ;


    private final String path;
    private final boolean manual;
    private final Object defaultMsg;

    /**
     * Create a local message used in the mini-game plugin.
     *
     * @param manual     true if this path requires manual saving to yml.
     *                   If message path has placeholders like this: my-path-{name}-lore.
     * @param path       message path.
     * @param defaultMsg default message for path.
     */
    Message(boolean manual, String path, Object defaultMsg) {
        this.path = path;
        this.manual = manual;
        this.defaultMsg = defaultMsg;
    }

    /**
     * Create a local message used in the mini-game plugin.
     *
     * @param path       message path.
     * @param defaultMsg default message for path.
     */
    Message(String path, Object defaultMsg) {
        this.path = path;
        this.manual = false;
        this.defaultMsg = defaultMsg;
    }

    /**
     * Check if this message needs manual saving.
     *
     * @return false if is saved by {@link #saveDefaults(YamlConfiguration)}.
     */
    public boolean isManual() {
        return manual;
    }

    /**
     * Get default message value.
     */
    public Object getDefaultMsg() {
        return defaultMsg;
    }

    /**
     * Save this message to a yml file.
     *
     * @param yml              language file where to save.
     * @param pathReplacements placeholders to be replaced in message path.
     * @param value            message value.
     */
    public void addDefault(YamlConfiguration yml, String[] pathReplacements, Object value) {
        String path = this.toString();
        for (int i = 0; i < pathReplacements.length; i += 2) {
            path = path.replace(pathReplacements[i], pathReplacements[i + 1]);
        }
        yml.addDefault(path, value);
    }

    /**
     * Save messages that are not {@link #isManual()} to the given yml.
     *
     * @param yml language file where to save.
     */
    public static void saveDefaults(YamlConfiguration yml) {
        for (Message message : values()) {
            if (!message.isManual()) {
                yml.addDefault(message.path, message.getDefaultMsg());
            }
        }
    }

    @Override
    public String toString() {
        return path;
    }
}
