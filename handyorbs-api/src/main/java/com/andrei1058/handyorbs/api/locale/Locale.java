package com.andrei1058.handyorbs.api.locale;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public interface Locale {

    /**
     * Color color translated message and
     * placeholder replacements.
     *
     * @param path   message path.
     * @param player used to parse placeholders. Nullable.
     * @return Chat color translated message at given path.
     */
    String getMsg(@Nullable Player player, String path);


    /**
     * Get a raw message. No color translation.
     * No placeholder parsing.
     */
    String getRawMsg(String path);

    /**
     * Get a raw message list. No color translation.
     * No placeholder parsing.
     */
    List<String> getRawList(String path);

    /**
     * Color color translated message.
     *
     * @param message message.
     * @param player  used to parse placeholders. Nullable.
     * @return Chat color translated message at given path.
     */
    default String getMsg(@Nullable Player player, Message message) {
        return getMsg(player, message.toString());
    }

    /**
     * Color color translated message.
     *
     * @param path   message path.
     * @param player player used to parse placeholders. Nullable.
     * @return Chat color translated message at given path.
     */
    default List<String> getMsgList(@Nullable Player player, String path) {
        return getMsgList(player, path, null);
    }

    /**
     * Color color translated message.
     *
     * @param path         message path.
     * @param player       player used to retrieve placeholders. Nullable.
     * @param replacements nullable.
     * @return Chat color translated message at given path.
     */
    List<String> getMsgList(@Nullable Player player, String path, String[] replacements);

    /**
     * Color color translated message.
     *
     * @param message message.
     * @param player  player used to parse placeholders. Nullable.
     * @return Chat color translated message at given path.
     */
    @SuppressWarnings("unused")
    default List<String> getMsgList(@Nullable Player player, Message message) {
        return getMsgList(player, message.toString());
    }

    /**
     * Get language iso code.
     * Languages are identified by this code.
     */
    String getIsoCode();

    /**
     * Reload translation file.
     */
    void reload();

    /**
     * Format date.
     *
     * @param date date to be formatted;
     */
    String formatDate(@Nullable Date date);

    /**
     * Get date format with time zone applied.
     */
    SimpleDateFormat getTimeZonedDateFormat();

    /**
     * Save a message at the given path if it does not exist.
     */
    void addDefault(String path, String message);

    /**
     * Save a message at the given path if it does not exist.
     */
    void addDefault(String path, List<String> message);

    /**
     * This will check if the language file is enabled via {@link Message#ENABLE}.
     */
    boolean isEnabled();
    /**
     * @param path message path.
     * @return True if the given path exists.
     */
    boolean hasPath(String path);

    /**
     * Set a string in the language file.
     *
     * @param path  msg path.
     * @param value msg.
     */
    void setMsg(String path, String value);

    /**
     * Set a string in the language file.
     *
     * @param path  msg path.
     * @param value msg.
     */
    void setList(String path, List<String> value);
}
