package com.andrei1058.handyorbs.language;

import com.andrei1058.handyorbs.HandyOrbsPlugin;
import com.andrei1058.handyorbs.api.event.PlayerChangeLocaleEvent;
import com.andrei1058.handyorbs.api.locale.Locale;
import com.andrei1058.handyorbs.api.locale.LocaleManager;
import com.andrei1058.handyorbs.api.locale.Message;
import com.andrei1058.handyorbs.config.MainConfig;
import com.andrei1058.handyorbs.database.repository.LanguageRepository;
import com.andrei1058.handyorbs.hook.HookManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

public class LanguageManager implements LocaleManager {

    private static LanguageManager INSTANCE;

    private final LinkedList<Locale> loadedLanguages = new LinkedList<>();
    private final HashMap<UUID, Locale> languageByPlayer = new HashMap<>();
    private Locale defaultLanguage;
    private File languagesFolder = new File(HandyOrbsPlugin.getInstance().getDataFolder(), "Locales");

    private LanguageManager() {
    }

    /**
     * Initialize Language Manager.
     */
    public static void onLoad() {
        if (INSTANCE != null) return;
        INSTANCE = new LanguageManager();

        // change languages directory if needed
        String languagesPath = MainConfig.getConfig().getProperty(MainConfig.LOCALE_FOLDER);
        if (!languagesPath.isEmpty()) {
            File newLanguagesFolder = new File(languagesPath);
            if (!newLanguagesFolder.isDirectory()) {
                HandyOrbsPlugin.getInstance().getLogger().severe("Tried to set languages path to: " + newLanguagesFolder + " but it does not seem to be a directory.");
            } else {
                getINSTANCE().languagesFolder = newLanguagesFolder;
                HandyOrbsPlugin.getInstance().getLogger().info("Set languages path to: " + newLanguagesFolder + ".");
            }
        }

        // create languages folder
        if (!INSTANCE.getLocalesFolder().exists()) {
            if (!INSTANCE.getLocalesFolder().mkdir()) {
                HandyOrbsPlugin.getInstance().getLogger().severe("Could not create directory: " + INSTANCE.getLocalesFolder().getPath());
            }
        }

        // define default language
        FallbackLanguage fallBackLanguage = new FallbackLanguage();
        FallbackLanguage.initDefaultMessages(fallBackLanguage);
        // it needs to be in the list
        INSTANCE.addLocale(fallBackLanguage);
        // then set
        INSTANCE.setDefaultLocale(fallBackLanguage);

        // load other languages
        for (File inFolder : Objects.requireNonNull(getINSTANCE().getLocalesFolder().listFiles())) {
            if (inFolder == null) continue;
            if (!inFolder.isFile()) continue;
            if (!inFolder.getName().endsWith(".yml")) continue;
            if (!inFolder.getName().startsWith("messages_")) continue;
            Language language = new Language(inFolder.getName().replace("messages_", "").replace(".yml", ""));
            getINSTANCE().addLocale(language);
        }

        // set default language from config
        String defaultIso = MainConfig.getConfig().getProperty(MainConfig.LOCALE_FALLBACK);
        Locale defaultLanguage = INSTANCE.getEnabledLocales().stream().filter(lang -> lang.getIsoCode().equals(defaultIso)).findFirst().orElse(null);
        if (defaultLanguage != null && getINSTANCE().setDefaultLocale(defaultLanguage)) {
            HandyOrbsPlugin.getInstance().getLogger().info("Set " + defaultIso + " as server's default language!");

            // unload fallback language if disabled
            if (!fallBackLanguage.getBoolean(Message.ENABLE.toString())) {
                getINSTANCE().removeLocale(fallBackLanguage);
            }
        } else {
            HandyOrbsPlugin.getInstance().getLogger().severe("Tried to set language " + defaultIso + " as server's default but it seems invalid!");
        }
    }

    /**
     * To be used in your plugin's onEnable.
     */
    public static void onEnable() {
        // register event
        if (MainConfig.getConfig().getProperty(MainConfig.LOCALE_SYNC_DB)) {
            Bukkit.getPluginManager().registerEvents(new JoinFetcher(), HandyOrbsPlugin.getInstance());
        }
        if (MainConfig.getConfig().getProperty(MainConfig.LOCALE_COMMAND)){
            //todo register command
        }
    }

    /**
     * Get Language Manager Instance.
     */
    public static LanguageManager getINSTANCE() {
        return INSTANCE;
    }

    /**
     * Replace player placeholders in given message.
     */
    private String replacePlaceholders(Player player, String message) {
        return HookManager.getInstance().getPapiHook().parsePlaceholders(player, message.replace("{vault_prefix}",
                HookManager.getInstance().getVaultChatHook().getPlayerPrefix(player)).replace("{vault_suffix}",
                HookManager.getInstance().getVaultChatHook().getPlayerSuffix(player)));
    }

    public @NotNull
    Locale getLocale(@NotNull Player player) {
        return getINSTANCE().languageByPlayer.getOrDefault(player.getUniqueId(), getINSTANCE().defaultLanguage);
    }

    @Override
    public @NotNull
    Locale getLocale(UUID player) {
        return getINSTANCE().languageByPlayer.getOrDefault(player, getINSTANCE().defaultLanguage);
    }

    @Override
    public String getMsg(@NotNull Player player, Message message) {
        return replacePlaceholders(player, getLocale(player).getMsg(player, message));
    }

    @Override
    public String getMsg(@NotNull CommandSender sender, Message message) {
        if (sender instanceof Player) {
            return getMsg((Player) sender, message);
        }
        return defaultLanguage.getMsg(null, message);
    }

    @Override
    public File getLocalesFolder() {
        return languagesFolder;
    }


    @Override
    public boolean addLocale(Locale translation) {
        if (!translation.hasPath(Message.NAME.toString())) return false;
        if (loadedLanguages.stream().anyMatch(lang -> lang.equals(translation))) return false;
        if (!translation.isEnabled()) return false;
        HandyOrbsPlugin.log("Adding language: " + translation.getRawMsg(Message.NAME.toString()));
        return loadedLanguages.add(translation);
    }

    @Override
    public boolean removeLocale(Locale translation) {
        if (translation.getIsoCode().equals(defaultLanguage.getIsoCode())) {
            return false;
        }
        // players to be switched to default language
        LinkedList<UUID> toSwitch = new LinkedList<>();
        languageByPlayer.forEach((uuid, translation1) -> {
            if (translation.equals(translation1)) {
                toSwitch.add(uuid);
            }
        });
        toSwitch.forEach(uuid -> {
            if (!setPlayerLocale(uuid, getDefaultLocale(), true)) {
                languageByPlayer.remove(uuid);
            }
        });
        boolean result = loadedLanguages.remove(translation);
        if (result) {
            HandyOrbsPlugin.log("Disabled language: " + translation.getMsg(null, Message.NAME));
        }
        return result;
    }

    @Override
    public Locale getDefaultLocale() {
        return defaultLanguage;
    }

    @Override
    public boolean setPlayerLocale(UUID uuid, Locale translation, boolean triggerEvent) {
        Locale old = LanguageManager.getINSTANCE().getLocale(uuid);
        if (translation == null) {
            languageByPlayer.remove(uuid);
            if (triggerEvent) {
                if (!old.equals(INSTANCE.getDefaultLocale())) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null) {
                        Bukkit.getPluginManager().callEvent(new PlayerChangeLocaleEvent(player, INSTANCE.getDefaultLocale(), old));
                    }
                }
            }
            return true;
        }
        if (loadedLanguages.stream().noneMatch(translation1 -> translation1.equals(translation))) return false;
        if (old.equals(INSTANCE.getDefaultLocale())) return false;

        if (translation.equals(getDefaultLocale())) {
            languageByPlayer.remove(uuid);
        } else {
            if (languageByPlayer.containsKey(uuid)) {
                languageByPlayer.replace(uuid, translation);
            } else {
                languageByPlayer.put(uuid, translation);
            }
        }
        if (triggerEvent) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                Bukkit.getPluginManager().callEvent(new PlayerChangeLocaleEvent(player, translation, old));
            }
        }

        // update db information
        if (MainConfig.getConfig().getProperty(MainConfig.LOCALE_SYNC_DB)) {
            Bukkit.getScheduler().runTaskAsynchronously(HandyOrbsPlugin.getInstance(), () -> {
                LanguageRepository.getInstance().setPlayerLanguage(uuid, translation);
            });
        }

        return true;
    }

    @Override
    public List<Locale> getEnabledLocales() {
        return Collections.unmodifiableList(loadedLanguages);
    }

    @Override
    public boolean setDefaultLocale(Locale translation) {
        if (loadedLanguages.stream().noneMatch(lang -> lang.equals(translation))) return false;
        if (translation instanceof Language) {
            FallbackLanguage.initDefaultMessages((Language) translation);
        }
        defaultLanguage = translation;
        return true;
    }

    @Override
    public @Nullable
    Locale getLocale(String isoCode) {
        return loadedLanguages.stream().filter(lang -> lang.getIsoCode().equals(isoCode)).findFirst().orElse(null);
    }

    @Override
    public boolean isLocaleExist(@Nullable String isoCode) {
        return getLocale(isoCode) != null;
    }

    @Override
    public String formatDate(Player player, @Nullable Date date) {
        return languageByPlayer.getOrDefault(player.getUniqueId(), getDefaultLocale()).formatDate(date);
    }

    @Override
    public String getMsg(Player player, String path) {
        return getLocale(player).getMsg(player, path);
    }
}
