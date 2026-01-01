package com.github.ojvzinn.sqlannotation.modules;

import com.github.ojvzinn.sqlannotation.SQL;
import com.github.ojvzinn.sqlannotation.annotations.Entity;
import com.github.ojvzinn.sqlannotation.model.ColumnModel;
import com.github.ojvzinn.sqlannotation.model.SQLTimerModel;
import com.github.ojvzinn.sqlannotation.utils.SQLUtils;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashMap;

public class CreateModule extends Module {

    public CreateModule(SQL instance) {
        super(instance);
    }

    public void scanEntity(Class<?> entity) {
        Entity tableName = SQLUtils.checkIfClassValid(entity);
        LinkedHashMap<String, Object> columns = new LinkedHashMap<>();
        boolean containsPrimaryKey = false;
        SQLTimerModel timer = new SQLTimerModel(System.currentTimeMillis());
        for (Field field : SQLUtils.listFieldColumns(entity)) {
            ColumnModel column = SQLUtils.makeColumn(field);
            if (column.isPrimaryKey()) {
                if (containsPrimaryKey) continue;
                containsPrimaryKey = true;
            }

            columns.put(column.getName(), column);
        }

        if (!containsPrimaryKey) {
            throw new RuntimeException("The table must contain a primary key");
        }

        String sql = getInstance().makeSQLCreateTable(tableName.name(), columns);
        try (Connection connection = getInstance().getDataSource().getConnection()) {
            connection.createStatement().execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("An error occurred while scanning the entity", e);
        }

        SQLUtils.loggingQuery(timer, sql);
        checkColumns(entity, columns);
    }

    public void checkColumns(Class<?> entity, LinkedHashMap<String, Object> columns) {
        Entity tableName = SQLUtils.checkIfClassValid(entity);
        SQLTimerModel timer = new SQLTimerModel(System.currentTimeMillis());
        String sql = getInstance().makeSQLCheckColumn(tableName.name(), columns);
        try (Connection connection = getInstance().getDataSource().getConnection()) {
            connection.createStatement().execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("An error occurred while loading columns", e);
        }

        SQLUtils.loggingQuery(timer, sql);
    }

}
