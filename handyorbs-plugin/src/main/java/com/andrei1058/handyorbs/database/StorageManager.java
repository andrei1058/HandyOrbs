package com.andrei1058.handyorbs.database;

import com.andrei1058.dbi.DatabaseAdapter;
import com.andrei1058.dbi.adapter.SQLiteAdapter;
import com.andrei1058.dbi.column.Column;
import com.andrei1058.dbi.column.ColumnValue;
import com.andrei1058.dbi.column.datavalue.SimpleValue;
import com.andrei1058.dbi.column.type.IntegerColumn;
import com.andrei1058.dbi.column.type.StringColumn;
import com.andrei1058.dbi.column.type.UUIDColumn;
import com.andrei1058.dbi.operator.EqualsOperator;
import com.andrei1058.dbi.operator.MultiEqualsOperator;
import com.andrei1058.dbi.table.Table;
import com.andrei1058.dbi.table.TableBuilder;
import com.andrei1058.handyorbs.api.OrbCategory;
import com.andrei1058.handyorbs.core.OrbBase;
import com.andrei1058.handyorbs.core.model.Ownable;
import com.andrei1058.handyorbs.registry.OrbRegistry;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class StorageManager {

    private static StorageManager instance;

    private final DatabaseAdapter databaseAdapter;
    private final Table orbsTable;
    private final Column<Integer> pk = new IntegerColumn("id", 0, 8);
    private final Column<UUID> ownerColumn = new UUIDColumn("owner", null);
    private final Column<Integer> chunkXColumn = new IntegerColumn("chunk_x", -1, 25);
    private final Column<Integer> chunkZColumn = new IntegerColumn("chunk_z", -1, 25);

    private final Column<Integer> locXColumn = new IntegerColumn("loc_x", -1, 25);
    private final Column<Integer> locYColumn = new IntegerColumn("loc_y", -1, 3);
    private final Column<Integer> locZColumn = new IntegerColumn("loc_z", -1, 25);
    private final Column<String> worldColumn = new StringColumn("loc_world", 20, null);

    private final Column<String> categoryColumn = new StringColumn("category", 20, null);
    private final Column<String> typeColumn = new StringColumn("type", 20, null);
    private final Column<String> nameColumn = new StringColumn("display_name", 128, null);
    private final Column<Integer> nameStatusColumn = new IntegerColumn("name_status", 1, 1);

    private StorageManager() throws SQLException {
        //todo add support for mysql
        databaseAdapter = new SQLiteAdapter("storage.db");
        TableBuilder tableBuilder = new TableBuilder("handyorbs", pk).autoIncrementPK()
                .withColumns(Arrays.asList(ownerColumn, chunkXColumn, chunkZColumn, locXColumn, locYColumn, locZColumn, worldColumn, categoryColumn, typeColumn
                        , nameColumn, nameStatusColumn));
        orbsTable = tableBuilder.build();
        databaseAdapter.createTable(orbsTable, false);
    }

    /**
     * @return false if could not init.
     */
    public static boolean init() {
        if (instance == null) {
            try {
                instance = new StorageManager();
            } catch (SQLException ignored) {
                return false;
            }
        }
        return true;
    }

    public static StorageManager getInstance() {
        return instance;
    }

    public List<OrbModel> getOrbsAtChunk(int x, int z, String world) {
        List<OrbModel> chunkOrbs = new ArrayList<>();
        List<Column<?>> columns = Arrays.asList(pk, locXColumn, locYColumn, locZColumn, categoryColumn, typeColumn, ownerColumn, nameColumn, nameStatusColumn);
        for (List<ColumnValue<?>> row : databaseAdapter.selectRows(columns,
                orbsTable, new MultiEqualsOperator(new SimpleValue<>(chunkXColumn, x), new SimpleValue<>(chunkZColumn, z), new SimpleValue<>(worldColumn, world)))) {
            // avoid issues
            if (row.size() != columns.size()) continue;

            int orbId = (Integer) row.remove(0).getValue();
            int locX = (Integer) row.remove(0).getValue();
            int locY = (Integer) row.remove(0).getValue();
            int locZ = (Integer) row.remove(0).getValue();
            Object categoryObj = row.remove(0).getValue();
            String category = categoryObj == null ? null : (String) categoryObj;
            Object typeObj = row.remove(0).getValue();
            String type = typeObj == null ? null : (String) typeObj;
            Object ownerObj = row.remove(0).getValue();
            UUID owner = ownerObj == null ? null : (UUID) ownerObj;
            Object nameObj = row.remove(0).getValue();
            String displayName = nameObj == null ? null : nameObj.toString();
            int nameStatus = (Integer) row.remove(0).getValue();

            OrbModel model = new OrbModel(orbId, locX, locY, locZ, world, category, type, owner, displayName, nameStatus);
            chunkOrbs.add(model);
        }
        return chunkOrbs;
    }

    public void saveUpdate(@NotNull OrbBase orbBase) {
        //if (databaseAdapter.select(pk, orbsTable, new EqualsOperator<>(pk, orbBase.getOrbId())) == -1){
        // insert
        OrbCategory orbCategory = OrbRegistry.getInstance().getActiveOrbCategory(orbBase.getOrbId());
        Bukkit.broadcastMessage("1");
        if (orbCategory == null) {
            orbCategory = OrbCategory.FARMING;
        }
        Bukkit.broadcastMessage("2");
        String orbIdentifier = OrbRegistry.getInstance().getActiveOrbIdentifier(orbBase);
        if (orbIdentifier == null) {
            orbIdentifier = "wheat";
        }
        Bukkit.broadcastMessage("3");

        List<ColumnValue<?>> values = new ArrayList<>();
        if (orbBase.getOrbId() == -1) {
            int newGeneratedId = databaseAdapter.getLastId(pk) + 1;
            orbBase.setOrbId(newGeneratedId);
        } else {
            values.add(new SimpleValue<>(pk, orbBase.getOrbId()));
        }
        values.add(new SimpleValue<>(ownerColumn, orbBase instanceof Ownable ? ((Ownable) orbBase).getOwner() : null));
        values.add(new SimpleValue<>(chunkXColumn, orbBase.getOrbEntity().getChunkX()));
        values.add(new SimpleValue<>(chunkZColumn, orbBase.getOrbEntity().getChunkZ()));
        values.add(new SimpleValue<>(locXColumn, (int) orbBase.getOrbEntity().getLocX()));
        values.add(new SimpleValue<>(locYColumn, (int) orbBase.getOrbEntity().getLocY()));
        values.add(new SimpleValue<>(locZColumn, (int) orbBase.getOrbEntity().getLocZ()));
        values.add(new SimpleValue<>(worldColumn, orbBase.getWorld()));
        values.add(new SimpleValue<>(categoryColumn, orbCategory.toString()));
        values.add(new SimpleValue<>(typeColumn, orbIdentifier));
        values.add(new SimpleValue<>(nameColumn, orbBase.getDisplayName()));
        values.add(new SimpleValue<>(nameStatusColumn, orbBase.getOrbEntity().getCustomNameVisible() ? 1 : 0));
        databaseAdapter.insert(orbsTable, values, DatabaseAdapter.InsertFallback.UPDATE);
        //} else {
        // update
        //}
    }
}
