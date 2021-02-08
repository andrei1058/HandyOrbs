package com.andrei1058.handyorbs.database;

import com.andrei1058.handyorbs.api.OrbCategory;
import com.andrei1058.handyorbs.core.OrbBase;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class OrbRepository {

    private static OrbRepository instance;
    private final ConnectionSource connectionSource;
    private Dao<OrbEntity, Integer> orbDao;

    private OrbRepository(String url) throws SQLException {
        connectionSource = new JdbcConnectionSource(url);
        orbDao = DaoManager.createDao(connectionSource, OrbEntity.class);
        TableUtils.createTableIfNotExists(connectionSource, OrbEntity.class);
    }

    /**
     * @return false if could not init.
     */
    public static boolean init(String url) {
        if (instance == null) {
            try {
                instance = new OrbRepository(url);
            } catch (SQLException ignored) {
                return false;
            }
        }
        return true;
    }

    public static OrbRepository getInstance() {
        return instance;
    }

    public List<OrbEntity> getOrbsAtChunk(int x, int z, String world) {
        List<OrbEntity> chunkOrbs;

        try {
            chunkOrbs = orbDao.queryBuilder().where().eq("chunkX", x)
                    .and().eq("chunkZ", z)
                    .and().eq("world", world).query();
        } catch (SQLException ex) {
            ex.printStackTrace();
            chunkOrbs = Collections.emptyList();
        }
        return chunkOrbs;
    }

    public void saveUpdate(@NotNull OrbBase orbBase, OrbCategory category) {
        try {
            OrbEntity orb = new OrbEntity(orbBase, category);
            orbDao.createOrUpdate(orb);
            orbBase.setOrbId(orb.getOrbId());
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
