package com.github.ojvzinn.sqlannotation;

import com.github.ojvzinn.sqlannotation.annotations.Entity;
import com.github.ojvzinn.sqlannotation.entity.ColumnEntity;
import com.github.ojvzinn.sqlannotation.entity.HikariEntity;
import com.github.ojvzinn.sqlannotation.modules.CreateModule;
import com.github.ojvzinn.sqlannotation.modules.DropModule;
import com.github.ojvzinn.sqlannotation.modules.InsertModule;
import com.github.ojvzinn.sqlannotation.modules.SelectModule;
import com.github.ojvzinn.sqlannotation.utils.SQLUtils;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

@Setter
@Getter
public abstract class SQL {

    private HikariDataSource dataSource = null;
    private CreateModule createModule = new CreateModule(this);
    private DropModule dropModule = new DropModule(this);
    private InsertModule insertModule = new InsertModule(this);
    private SelectModule selectModule = new SelectModule(this);

    public void checkColumn(Class<?> entity, String fieldColumn) {
        Entity tableName = SQLUtils.checkIfClassValid(entity);
        Field field;
        try {
            field = entity.getDeclaredField(fieldColumn);
        } catch (Exception e) {
            throw new RuntimeException("There is no column with that name in your table.");
        }

        ColumnEntity column = SQLUtils.makeColumn(field);
        try (Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();
            String sql = makeSQLCheckColumn(tableName.name(), column.getName(), column.makeType());
            statement.execute(sql);
            SQLUtils.loggingQuery(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public abstract void init(HikariEntity entity);

    public abstract void destroy();

    public abstract String makeSQLCreateTable(String table, Map<String, ColumnEntity> columns);

    public abstract String makeSQLCheckColumn(String table, String column, String type);

}
