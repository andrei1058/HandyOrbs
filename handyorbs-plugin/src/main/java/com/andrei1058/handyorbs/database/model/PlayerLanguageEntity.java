package com.andrei1058.handyorbs.database.model;

import com.andrei1058.handyorbs.api.locale.Locale;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.util.UUID;

@DatabaseTable(tableName = "player_language")
public class PlayerLanguageEntity {

    @DatabaseField(generatedId = true, useGetSet = true)
    private int id;
    @DatabaseField(useGetSet = true, unique = true)
    private UUID player;
    @DatabaseField(canBeNull = false, useGetSet = true)
    private String language;

    public PlayerLanguageEntity(UUID player, Locale locale){
        this.setPlayer(player);
        this.setLanguage(locale.getIsoCode());
    }

    @SuppressWarnings("unused")
    public PlayerLanguageEntity() {
        // ORM Constructor
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public UUID getPlayer() {
        return player;
    }

    public void setPlayer(UUID player) {
        this.player = player;
    }
}
