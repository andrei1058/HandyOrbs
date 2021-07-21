package com.andrei1058.handyorbs.database.repository;

import com.andrei1058.handyorbs.api.locale.Locale;
import com.andrei1058.handyorbs.database.model.PlayerLanguageEntity;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class LanguageRepository {

    private static LanguageRepository instance;
    private final Dao<PlayerLanguageEntity, Integer> languageDao;

    private LanguageRepository(ConnectionSource connectionSource) throws SQLException {
        languageDao = DaoManager.createDao(connectionSource, PlayerLanguageEntity.class);
        TableUtils.createTableIfNotExists(connectionSource, PlayerLanguageEntity.class);
    }

    public static void init(ConnectionSource connectionSource) throws SQLException {
        if (instance == null){
            instance = new LanguageRepository(connectionSource);
        }
    }

    /**
     * Make sure to use it async!
     */
    public PlayerLanguageEntity setPlayerLanguage(@NotNull UUID player, Locale language) {
        try {
            PlayerLanguageEntity locale = getByPlayer(player);
            if (locale == null){
                locale = new PlayerLanguageEntity(player, language);
            } else {
               locale.setLanguage(language.getIsoCode());
            }
            languageDao.createOrUpdate(locale);
            languageDao.update(locale);
            return locale;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Watch out what you're doing. This should be used async!
     */
    public PlayerLanguageEntity getByPlayer(UUID uuid){
        List<PlayerLanguageEntity> result = null;
        try {
             result =  languageDao.queryForEq("player", uuid);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result == null ? null : result.isEmpty() ? null : result.get(0);
    }

    public static LanguageRepository getInstance() {
        return instance;
    }
}
