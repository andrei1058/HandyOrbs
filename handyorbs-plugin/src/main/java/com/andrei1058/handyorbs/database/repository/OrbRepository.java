package com.andrei1058.handyorbs.database.repository;

import com.andrei1058.handyorbs.HandyOrbsPlugin;
import com.andrei1058.handyorbs.api.OrbCategory;
import com.andrei1058.handyorbs.core.OrbBase;
import com.andrei1058.handyorbs.core.model.Ownable;
import com.andrei1058.handyorbs.core.model.TimedOrb;
import com.andrei1058.handyorbs.database.model.OrbEntity;
import com.andrei1058.handyorbs.registry.OrbRegistry;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class OrbRepository {

    private static OrbRepository instance;
    private final Dao<OrbEntity, Integer> orbDao;

    private OrbRepository(ConnectionSource connectionSource) throws SQLException {
        orbDao = DaoManager.createDao(connectionSource, OrbEntity.class);
        TableUtils.createTableIfNotExists(connectionSource, OrbEntity.class);
    }

    public static void init(ConnectionSource connectionSource) throws SQLException {
        if (instance == null){
            instance = new OrbRepository(connectionSource);
        }
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

    public void updateOrbName(OrbBase orbBase) {
        Bukkit.getScheduler().runTaskAsynchronously(HandyOrbsPlugin.getInstance(), ()-> {
            OrbEntity orb = getOrbById(orbBase.getOrbId());
            if (orb != null){
                orb.setDisplayName(orbBase.getDisplayName());
                try {
                    orbDao.createOrUpdate(orb);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        });
    }

    public void updateOrbNameVisibility(OrbBase orbBase) {
        Bukkit.getScheduler().runTaskAsynchronously(HandyOrbsPlugin.getInstance(), ()-> {
            OrbEntity orb = getOrbById(orbBase.getOrbId());
            if (orb != null){
                orb.setNameStatus(orbBase.getOrbEntity().getCustomNameVisible());
                try {
                    orbDao.createOrUpdate(orb);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        });
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
                if (orbBase instanceof TimedOrb){
                    orb.setActivityDelay(((TimedOrb) orbBase).getActivityDelay());
                }
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
