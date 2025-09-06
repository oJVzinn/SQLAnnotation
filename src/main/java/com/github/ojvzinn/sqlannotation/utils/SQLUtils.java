package com.github.ojvzinn.sqlannotation.utils;

import com.github.ojvzinn.sqlannotation.annotations.Column;
import com.github.ojvzinn.sqlannotation.annotations.Entity;
import com.github.ojvzinn.sqlannotation.annotations.PrimaryKey;
import com.github.ojvzinn.sqlannotation.annotations.Varchar;
import com.github.ojvzinn.sqlannotation.entity.ColumnEntity;
import com.github.ojvzinn.sqlannotation.enums.ClassType;
import com.github.ojvzinn.sqlannotation.logger.SQLogger;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class SQLUtils {

    private static final SQLogger logger = new SQLogger("SQL");

    public static Entity checkIfClassValid(Class<?> entity) {
        Entity tableName = entity.getAnnotation(Entity.class);
        if (tableName == null) {
            throw new RuntimeException("The table class needs to come with the @Table annotation");
        }

        return tableName;
    }

    public static ColumnEntity makeColumn(Field field) {
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

    public static LinkedList<Field> listFieldColumns(Class<?> entity) {
        Field[] fields = entity.getDeclaredFields();
        if (fields.length == 0) {
            throw new RuntimeException("To create a table it is necessary to have at least one column field");
        }

        return Arrays.stream(fields).filter(field -> field.getAnnotation(Column.class) != null).collect(Collectors.toCollection(LinkedList::new));
    }

    public static Field findPrimaryKey(Class<?> entity) {
        Field fieldKey = Arrays.stream(entity.getDeclaredFields()).filter(field -> field.getAnnotation(PrimaryKey.class) != null).findFirst().orElse(null);
        if (fieldKey == null) {
            throw new RuntimeException("There is no primary key column in your table");
        }

        return fieldKey;
    }

    public static <T> T loadClass(Class<T> entity, JSONObject values) {
        T instance;
        try {
            Constructor<T> constructor = entity.getDeclaredConstructor();
            constructor.setAccessible(true);
            instance = constructor.newInstance();
            for (Field field : SQLUtils.listFieldColumns(entity)) {
                if (!values.keySet().contains(field.getName())) {
                    continue;
                }

                field.setAccessible(true);
                field.set(instance, values.get(field.getName()));
            }
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return instance;
    }

    public static void loggingQuery(String sql) {
        logger.info("QUERY EXECUTED: " + sql);
    }
}
