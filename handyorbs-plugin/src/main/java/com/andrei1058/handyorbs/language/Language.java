package com.andrei1058.handyorbs.language;

import com.andrei1058.handyorbs.HandyOrbsPlugin;
import com.andrei1058.handyorbs.api.locale.Locale;
import com.andrei1058.handyorbs.api.locale.Message;
import com.andrei1058.handyorbs.hook.HookManager;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Language extends YamlConfiguration implements Locale {

    private YamlConfiguration yml;
    private final String isoCode;
    private File languageFile;
    private String prefix = "";

    protected Language(String isoCode) {
        this.isoCode = isoCode;
        reload();
    }

    @Override
    public void setMsg(String path, String value) {
        yml.set(path, value);
        save();
    }

    @Override
    public void setList(String path, List<String> value) {
        yml.set(path, value);
        save();
    }

    @Override
    public boolean hasPath(String path) {
        return yml.get(path) != null;
    }

    @Override
    public String getMsg(@Nullable Player player, String path) {
        Object o = getYml().get(path);
        if (o == null && !(this instanceof FallbackLanguage)) {
            return LanguageManager.getINSTANCE().getDefaultLocale().getMsg(player, path);
        }
        if (o instanceof String) {
            return ChatColor.translateAlternateColorCodes('&', replacePlaceholders(player, (String) o));
        } else if (o instanceof List) {
            StringBuilder stringBuilder = new StringBuilder();
            for (Object s : ((List<?>) o)) {
                stringBuilder.append(ChatColor.translateAlternateColorCodes('&', replacePlaceholders(player, s.toString()))).append("\n");
            }
            return stringBuilder.toString();
        }
        return o == null ? "" : o.toString();
    }

    @Override
    public String getRawMsg(String path) {
        Object o = getYml().get(path);
        if (o == null && !(this instanceof FallbackLanguage)) {
            return LanguageManager.getINSTANCE().getDefaultLocale().getRawMsg(path);
        }
        if (o instanceof String) {
            return (String) o;
        } else if (o instanceof List) {
            StringBuilder stringBuilder = new StringBuilder();
            for (Object s : ((List<?>) o)) {
                stringBuilder.append(s.toString()).append("\n");
            }
            return stringBuilder.toString();
        }
        return o == null ? "" : o.toString();
    }

    @Override
    public List<String> getRawList(String path) {
        Object o = getYml().get(path);
        if (o == null && !(this instanceof FallbackLanguage)) {
            return LanguageManager.getINSTANCE().getDefaultLocale().getRawList(path);
        }
        if (o instanceof List<?>) {
            //noinspection unchecked
            return (List<String>) o;
        }
        return o == null ? new ArrayList<>() : Collections.singletonList(o.toString());
    }

    @Override
    public List<String> getMsgList(Player player, String path, String[] replacements) {
        Object o = getYml().get(path);
        if (o == null && !(this instanceof FallbackLanguage)) {
            return LanguageManager.getINSTANCE().getDefaultLocale().getMsgList(player, path, replacements);
        }
        if (o instanceof List) {
            List<String> list = new ArrayList<>();

            if (replacements != null && replacements.length > 1) {
                for (Object s : ((List<?>) o)) {
                    String toAdd = s.toString();
                    for (int i = 0; i < replacements.length; i += 2) {
                        toAdd = toAdd.replace(replacements[i], replacements[i + 1]);
                    }
                    Collections.addAll(list, ChatColor.translateAlternateColorCodes('&', replacePlaceholders(player, toAdd)).split("\\n"));
                }
                return list;
            }

            ((List<?>) o).forEach(s -> list.add(ChatColor.translateAlternateColorCodes('&', replacePlaceholders(player, s.toString()))));
            return list;
        }
        return o == null ? new ArrayList<>() : Collections.singletonList(o.toString());
    }

    @Override
    public String getIsoCode() {
        return isoCode;
    }

    @Override
    public boolean isEnabled() {
        return getYml().getBoolean(Message.ENABLE.toString());
    }

    @Override
    public void reload() {
        languageFile = new File(LanguageManager.getINSTANCE().getLocalesFolder(), "messages_" + isoCode + ".yml");
        if (!languageFile.exists()) {
            HandyOrbsPlugin.getInstance().getLogger().info("Creating: " + languageFile.getPath());
            try {
                if (!languageFile.createNewFile()) {
                    if (this instanceof FallbackLanguage) {
                        HandyOrbsPlugin.getInstance().getLogger().severe("Could not create default language file: " + languageFile.getPath());
                    } else {
                        HandyOrbsPlugin.getInstance().getLogger().severe("Could not create file: " + languageFile.getPath());
                    }
                }
            } catch (IOException e) {
                HandyOrbsPlugin.getInstance().getLogger().severe("Could not create file: " + languageFile.getPath());
            }
        }
        yml = YamlConfiguration.loadConfiguration(languageFile);

        prefix = hasPath(Message.PREFIX.toString()) ? ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(yml.getString(Message.PREFIX.toString()))) :
                LanguageManager.getINSTANCE().getDefaultLocale() == null ? "" : LanguageManager.getINSTANCE().getDefaultLocale().getMsg(null, Message.PREFIX);
    }

    @Override
    public String formatDate(@Nullable Date date) {
        if (date == null) return getMsg(null, Message.DATE_NONE);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getRawMsg(Message.DATE_FORMAT.toString()));
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(getRawMsg(Message.TIME_ZONE.toString())));
        return simpleDateFormat.format(date);
    }

    @Override
    public SimpleDateFormat getTimeZonedDateFormat() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getRawMsg(Message.DATE_FORMAT.toString()));
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(getRawMsg(Message.TIME_ZONE.toString())));
        return simpleDateFormat;
    }

    @Override
    public void addDefault(String path, String message) {
        if (!hasPath(path)){
            setMsg(path, message);
            save();
        }
    }

    @Override
    public void addDefault(String path, List<String> message) {
        if (!hasPath(path)){
            setList(path, message);
            save();
        }
    }

    public String getPrefix() {
        return prefix;
    }

    public YamlConfiguration getYml() {
        return yml;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof Locale) {
            return ((Locale) o).getIsoCode().equals(getIsoCode());
        }
        return false;
    }

    @SuppressWarnings("unused")
    protected void addDefault(Message message, Object value) {
        getYml().addDefault(message.toString(), value);
    }

    public void save() {
        try {
            getYml().save(languageFile);
        } catch (IOException e) {
            HandyOrbsPlugin.getInstance().getLogger().severe("Could not save language: " + getIsoCode());
        }
    }

    /**
     * Replace player placeholders in given message.
     */
    protected String replacePlaceholders(Player player, String message) {
        return HookManager.getInstance().getPapiHook().parsePlaceholders(player, message.replace("{vault_prefix}",
                HookManager.getInstance().getVaultChatHook().getPlayerPrefix(player)).replace("{vault_suffix}",
                HookManager.getInstance().getVaultChatHook().getPlayerSuffix(player))).replace("{prefix}", getPrefix());
    }
}
