package com.andrei1058.handyorbs.database;

import com.andrei1058.handyorbs.api.OrbCategory;
import com.andrei1058.handyorbs.core.OrbBase;
import com.andrei1058.handyorbs.core.model.Ownable;
import com.andrei1058.handyorbs.registry.OrbRegistry;
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
    private final Dao<OrbEntity, Integer> orbDao;

    private OrbRepository(String url) throws SQLException {
        ConnectionSource connectionSource = new JdbcConnectionSource(url);
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

    public OrbEntity getOrbById(int id){
        try {
            return orbDao.queryForId(id);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public void markOrbAsRemovedFromGround(int id){
        OrbEntity orb = getOrbById(id);
        if (orb != null){
            orb.setChunkX(null);
            orb.setChunkZ(null);
            try {
                orbDao.createOrUpdate(orb);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public OrbEntity saveUpdate(@NotNull OrbBase orbBase, OrbCategory category) {
        try {
            OrbEntity orb = getOrbById(orbBase.getOrbId());
            if (orb == null){
                orb = new OrbEntity(orbBase, category);
            } else {
                orb.setLocX((int) orbBase.getOrbEntity().getLocX());
                orb.setLocY((int) orbBase.getOrbEntity().getLocY());
                orb.setLocZ((int) orbBase.getOrbEntity().getLocZ());
                orb.setWorld(orbBase.getWorld());

                OrbCategory orbCategory = category == null ? OrbRegistry.getInstance().getActiveOrbCategory(orbBase.getOrbId()) : category;
                if (orbCategory == null) {
                    throw new IllegalStateException("Given orb does not have a valid category!");
                }
                String orbIdentifier = OrbRegistry.getInstance().getActiveOrbIdentifier(orbBase);
                if (orbIdentifier == null) {
                    throw new IllegalStateException("Given orb does not have a valid type!");
                }
                orb.setCategory(orbCategory.name());
                orb.setType(orbIdentifier);
                orb.setOwner(orbBase instanceof Ownable ? ((Ownable) orbBase).getOwner() : null);
                orb.setDisplayName(orbBase.getDisplayName());
                orb.setNameStatus(orbBase.getOrbEntity().getCustomNameVisible());
                orb.setChunkX(orbBase.getOrbEntity().getChunkX());
                orb.setChunkZ(orbBase.getOrbEntity().getChunkZ());
            }
            orb.setRegion(orbBase.getRegion().toExport());
            orbDao.createOrUpdate(orb);
            orbDao.update(orb);
            return orb;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
