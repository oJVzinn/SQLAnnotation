package com.github.ojvzinn.sqlannotation;

import com.github.ojvzinn.sqlannotation.annotations.Column;
import com.github.ojvzinn.sqlannotation.annotations.PrimaryKey;
import com.github.ojvzinn.sqlannotation.annotations.Table;
import com.github.ojvzinn.sqlannotation.annotations.Varchar;
import com.github.ojvzinn.sqlannotation.entity.ColumnEntity;
import com.github.ojvzinn.sqlannotation.entity.HikariEntity;
import com.github.ojvzinn.sqlannotation.enums.ClassType;
import com.github.ojvzinn.sqlannotation.logger.SQLogger;
import com.github.ojvzinn.sqlannotation.utils.SQLUtils;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

@Setter
@Getter
public abstract class SQL {

    private HikariDataSource dataSource = null;
    private SQLogger logger = new SQLogger("SQL");

    public void scanTable(Class<?> tableClass) {
        Table tableName = tableClass.getAnnotation(Table.class);
        if (tableName == null) {
            throw new RuntimeException("The table class needs to come with the @Table annotation");
        }

        LinkedHashMap<String, ColumnEntity> columns = new LinkedHashMap<>();
        Field[] fields = tableClass.getDeclaredFields();
        boolean containsPrimaryKey = false;
        if (fields.length == 0) {
            throw new RuntimeException("To create a table it is necessary to have at least one column field");
        }

        for (Field field : Arrays.stream(fields).filter(field -> field.getAnnotation(Column.class) != null).collect(Collectors.toList())) {
            ColumnEntity column = makeColumn(field);
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

        try (Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();
            String sql = makeSQLCreateTable(tableName.name(), columns);
            statement.execute(sql);
            loggingQuery(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void dropTable(Class<?> tableClass) {
        Table tableName = tableClass.getAnnotation(Table.class);
        if (tableName == null) {
            throw new RuntimeException("The table class needs to come with the @Table annotation");
        }

        try (Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();
            String sql = "DROP TABLE " + tableName.name();
            statement.execute(sql);
            loggingQuery(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void checkColumn(Class<?> tableClass, String fieldColumn) {
        Table tableName = tableClass.getAnnotation(Table.class);
        if (tableName == null) {
            throw new RuntimeException("The table class needs to come with the @Table annotation");
        }

        Field field;
        try {
            field = tableClass.getDeclaredField(fieldColumn);
        } catch (Exception e) {
            throw new RuntimeException("There is no column with that name in your table.");
        }

        ColumnEntity column = makeColumn(field);
        try (Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();
            String sql = makeSQLCheckColumn(tableName.name(), column.getName(), column.makeType());
            statement.execute(sql);
            loggingQuery(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T findByKey(Class<T> tableClass, Object key) {
        Table tableName = tableClass.getAnnotation(Table.class);
        if (tableName == null) {
            throw new RuntimeException("The table class needs to come with the @Table annotation");
        }

        Field fieldKey = Arrays.stream(tableClass.getDeclaredFields()).filter(field -> field.getAnnotation(PrimaryKey.class) != null).findFirst().orElse(null);
        if (fieldKey == null) {
            throw new RuntimeException("There is no primary key column in your table");
        }

        ColumnEntity column = makeColumn(fieldKey);
        List<Map<String, Object>> resultAll = select(tableName.name(), column.getName(), "=", key.toString());
        if (resultAll.isEmpty()) {
            return null;
        }

        Map<String, Object> result = resultAll.stream().findFirst().get();
        T instance;
        try {
            Constructor<T> constructor = tableClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            instance = constructor.newInstance();
            Field[] fields = tableClass.getDeclaredFields();
            for (Field field : fields) {
                if (!result.containsKey(field.getName())) {
                    continue;
                }

                field.setAccessible(true);
                field.set(instance, result.get(field.getName()));
            }
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return instance;
    }

    private void loggingQuery(String sql) {
        logger.info("QUERY EXECUTED: " + sql);
    }

    private ColumnEntity makeColumn(Field field) {
        Column column = field.getAnnotation(Column.class);
        PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
        Varchar varchar = field.getAnnotation(Varchar.class);
        String columnName = field.getName();
        ClassType type = ClassType.getType(field.getType());
        if (type == null) {
            throw new RuntimeException("The field type is invalid");
        }

        boolean autoIncrement = false;
        int size = varchar != null ? varchar.length() : 0;
        if ((size <= 0 || size > 255) && type == ClassType.VARCHAR) {
            throw new RuntimeException("Invalid varchar size value");
        }

        if (primaryKey != null) {
            autoIncrement = primaryKey.autoIncrement();
        }

        return new ColumnEntity(columnName, type.getType(), column.notNull(), autoIncrement, primaryKey != null, size);
    }

    private List<Map<String, Object>> select(String table, String column, String conditional, String value) {
        List<Map<String, Object>> result = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM ").append(table).append(" WHERE ").append(column).append(" ").append(conditional).append(" ?");
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql.toString());
            statement.setObject(1, value);
            try (ResultSet resultSet = statement.executeQuery()) {
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();
                while (resultSet.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        row.put(metaData.getColumnLabel(i), resultSet.getObject(i));
                    }

                    result.add(row);
                }
            }

            loggingQuery(sql.toString());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    public abstract void init(HikariEntity entity);

    public abstract void destroy();

    public abstract String makeSQLCreateTable(String table, Map<String, ColumnEntity> columns);

    public abstract String makeSQLCheckColumn(String table, String column, String type);

}
