package com.andrei1058.handyorbs.database;

import com.andrei1058.handyorbs.database.repository.LanguageRepository;
import com.andrei1058.handyorbs.database.repository.OrbRepository;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

public class DatabaseManager {

    private static DatabaseManager instance;

    public DatabaseManager(String url, String username, String password) throws SQLException {
        ConnectionSource connectionSource = new JdbcConnectionSource(url, username, password);
        OrbRepository.init(connectionSource);
        LanguageRepository.init(connectionSource);
    }

    public static void init(String url,  String username, String password) throws SQLException {
        if (instance == null){
            instance = new DatabaseManager(url, username, password);
        }
    }
}
