package com.github.ojvzinn.sqlannotation.utils;

import com.github.ojvzinn.sqlannotation.annotations.*;
import com.github.ojvzinn.sqlannotation.model.ColumnModel;
import com.github.ojvzinn.sqlannotation.model.SQLTimerModel;
import com.github.ojvzinn.sqlannotation.enums.ClassType;
import com.github.ojvzinn.sqlannotation.logger.SQLogger;
import com.github.ojvzinn.sqlannotation.model.SelectJoinModel;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class SQLUtils {

    private static final SQLogger logger = new SQLogger("SQL");

    public static Entity checkIfClassValid(Class<?> entity) {
        Entity tableName = entity.getAnnotation(Entity.class);
        if (tableName == null) throw new RuntimeException("The table class needs to come with the @Entity annotation");

        return tableName;
    }

    public static Object getValueJoinField(Field joinEntityField, Object joinEntity) {
        try {
            Join join = joinEntityField.getAnnotation(Join.class);
            Field joinParameter = joinEntity.getClass().getDeclaredField(join.column());
            joinParameter.setAccessible(true);
            return joinParameter.get(joinEntity);
        } catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {
            throw new RuntimeException("Error processing the value of the join entity", e);
        }
    }

    public static boolean isJoinField(Field joinEntityField, Object joinEntity) {
        return joinEntityField.getAnnotation(Join.class) != null && joinEntity.getClass().getAnnotation(Entity.class) != null;
    }

    public static boolean containsEntity(Class<?> entity) {
        return Arrays.stream(entity.getDeclaredFields()).anyMatch(field -> field.getType().isAnnotationPresent(Entity.class));
    }

    public static SelectJoinModel getSelectJoinModel(Class<?> entity) {
        return SQLUtils.containsEntity(entity) ? new SelectJoinModel(entity) : null;
    }

    public static ColumnModel makeColumn(Field field) {
        Column column = field.getAnnotation(Column.class);
        PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
        Varchar varchar = field.getAnnotation(Varchar.class);
        String columnName = field.getName();
        ClassType type = ClassType.getType(field.getType());
        Join join = field.getAnnotation(Join.class);
        if (join != null) {
            try {
                type = ClassType.getType(field.getType().getDeclaredField(join.column()).getType());
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }

        if (type == null) throw new RuntimeException("The field type is invalid");

        int size = varchar != null ? varchar.length() : 0;
        if ((size <= 0 || size > 255) && type == ClassType.VARCHAR) throw new RuntimeException("Invalid varchar size value");

        boolean autoIncrement = primaryKey != null && primaryKey.autoIncrement();

        return new ColumnModel(columnName, type.getType(), column.notNull(), autoIncrement, primaryKey != null, column.unique(), size);
    }

    public static LinkedList<Field> listFieldColumns(Class<?> entity, boolean spliceJoinFields) {
        Field[] fields = entity.getDeclaredFields();
        if (fields.length == 0) throw new RuntimeException("To create a table it is necessary to have at least one column field");

        return Arrays.stream(fields).filter(field -> field.getAnnotation(Column.class) != null && (!spliceJoinFields || field.getAnnotation(Join.class) == null)).collect(Collectors.toCollection(LinkedList::new));
    }

    public static Field findPrimaryKey(Class<?> entity) {
        Field fieldKey = Arrays.stream(entity.getDeclaredFields()).filter(field -> field.getAnnotation(PrimaryKey.class) != null).findFirst().orElse(null);
        if (fieldKey == null) throw new RuntimeException("There is no primary key column in your table");

        return fieldKey;
    }

    public static <T> T loadClass(Class<T> entity, JSONObject values, SelectJoinModel joinModel) {
        T instance;
        try {
            Constructor<T> constructor = entity.getDeclaredConstructor();
            constructor.setAccessible(true);
            instance = constructor.newInstance();
            List<Object> joinEntities = loadJoinEntity(joinModel, values);
            for (Field field : SQLUtils.listFieldColumns(entity, false)) {
                String finalColumn = getFinalColumnName(field.getName(), joinModel);
                if (!values.keySet().contains(finalColumn)) continue;

                field.setAccessible(true);
                field.set(instance, !joinEntities.isEmpty() && field.getAnnotation(Join.class) != null ? findJoinEntityByField(field, joinEntities) : values.get(finalColumn));
            }
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("An error occurred while loading your entity class. Report this to developer \"oJVzinn\"", e);
        }

        return instance;
    }

    public static void loggingQuery(SQLTimerModel timer, String sql) {
        logger.info("QUERY EXECUTED: " + sql + ". Was executed in " + timer.stop() + " ms.");
    }

    private static List<Object> loadJoinEntity(SelectJoinModel joinModel, JSONObject values) {
        if (joinModel == null) return null;
        List<Object> entities = new ArrayList<>();
        try {
            for (Class<?> entityClass : joinModel.findJoinEntitiesClass()) {
                Constructor<?> constructor = entityClass.getDeclaredConstructor();
                constructor.setAccessible(true);
                Object entity = constructor.newInstance();
                for (Field field : SQLUtils.listFieldColumns(entityClass, false)) {
                    String columnName = joinModel.getTableReference(entityClass) + "_" + field.getName();
                    if (!values.keySet().contains(columnName)) continue;

                    field.setAccessible(true);
                    field.set(entity, values.get(columnName));
                }

                entities.add(entity);
            }

            return entities;
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("The relationship entity could not be loaded.", e);
        }
    }

    private static Object findJoinEntityByField(Field field, List<Object> entities) {
        return entities.stream().filter(entity -> entity.getClass().isAssignableFrom(field.getType())).findFirst().orElse(null);
    }

    private static String getFinalColumnName(String field, SelectJoinModel joinModel) {
        return joinModel != null ? joinModel.getEntityTableReference() + "_" + field : field;
    }
}
