package com.andrei1058.handyorbs.language;

import com.andrei1058.handyorbs.api.locale.Message;
import org.bukkit.configuration.file.YamlConfiguration;


public class FallbackLanguage extends Language {

    protected FallbackLanguage() {
        super("en");
    }

    public static void initDefaultMessages(Language lang) {
        lang.getYml().options().copyDefaults(true);
        YamlConfiguration yml = lang.getYml();

        if (lang instanceof FallbackLanguage) {
            yml.addDefault(Message.NAME.toString(), "English");
            lang.save();
        }

        // save default messages that do not require manual implementations
        Message.saveDefaults(yml);

        lang.getYml().options().header("If you want to disable this language set enable: false own bellow. This option is ignored for the default server language");
        lang.getYml().options().copyDefaults(true);
        lang.save();
    }
}
