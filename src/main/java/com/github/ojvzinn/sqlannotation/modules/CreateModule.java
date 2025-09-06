package com.github.ojvzinn.sqlannotation.modules;

import com.github.ojvzinn.sqlannotation.SQL;
import com.github.ojvzinn.sqlannotation.annotations.Entity;
import com.github.ojvzinn.sqlannotation.entity.ColumnEntity;
import com.github.ojvzinn.sqlannotation.utils.SQLUtils;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;

public class CreateModule extends Module {

    public CreateModule(SQL instance) {
        super(instance);
    }

    public void scanEntity(Class<?> entity) {
        Entity tableName = SQLUtils.checkIfClassValid(entity);
        LinkedHashMap<String, Object> columns = new LinkedHashMap<>();
        boolean containsPrimaryKey = false;
        for (Field field : SQLUtils.listFieldColumns(entity)) {
            ColumnEntity column = SQLUtils.makeColumn(field);
            if (column.isPrimaryKey()) {
                if (containsPrimaryKey) {
                    continue;
                }

                containsPrimaryKey = true;
            }

            columns.put(column.getName(), column);
        }

        if (!containsPrimaryKey) {
            throw new RuntimeException("The table must contain a primary key");
        }

        try (Connection connection = getInstance().getDataSource().getConnection()) {
            Statement statement = connection.createStatement();
            String sql = getInstance().makeSQLCreateTable(tableName.name(), columns);
            statement.execute(sql);
            SQLUtils.loggingQuery(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
