package com.github.ojvzinn.sqlannotation.utils;

import com.github.ojvzinn.sqlannotation.SQLAnnotation;
import com.github.ojvzinn.sqlannotation.annotations.*;
import com.github.ojvzinn.sqlannotation.model.ColumnModel;
import com.github.ojvzinn.sqlannotation.model.SQLTimerModel;
import com.github.ojvzinn.sqlannotation.enums.ClassType;
import com.github.ojvzinn.sqlannotation.logger.SQLogger;
import com.github.ojvzinn.sqlannotation.model.SelectJoinModel;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
        Class<?> targetType = field.getType();
        Join join = field.getAnnotation(Join.class);
        if (join != null) {
            try {
                targetType = field.getType().getDeclaredField(join.column()).getType();
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }

        ClassType type = ClassType.getType(targetType, varchar != null);
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

    public static <T> T loadClass(Class<T> entity, Map<String, Object> values, SelectJoinModel joinModel) {
        T instance;
        try {
            Constructor<T> constructor = entity.getDeclaredConstructor();
            constructor.setAccessible(true);
            instance = constructor.newInstance();
            List<Object> joinEntities = loadJoinEntity(joinModel, values);
            for (Field field : SQLUtils.listFieldColumns(entity, false)) {
                String finalColumn = getFinalColumnName(field.getName(), joinModel);
                if (!values.containsKey(finalColumn)) continue;

                field.setAccessible(true);
                field.set(instance, joinEntities != null && !joinEntities.isEmpty() && field.getAnnotation(Join.class) != null ? findJoinEntityByField(field, joinEntities) : convertValue(values.get(finalColumn), field.getType()));
            }
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("An error occurred while loading your entity class. Report this to developer \"oJVzinn\"", e);
        }

        return instance;
    }

    public static void loggingQuery(SQLTimerModel timer, String sql) {
        if (SQLAnnotation.getConfig() != null && SQLAnnotation.getConfig().isLog()) {
            logger.info("QUERY EXECUTED: " + sql + ". Was executed in " + timer.stop() + " ms.");
        }
    }

    public static Object convertValue(Object rawValue, Class<?> targetType) {
        if (rawValue == null) {
            if (targetType == boolean.class) return false;
            if (targetType == int.class) return 0;
            if (targetType == long.class) return 0L;
            if (targetType == double.class) return 0.0d;
            if (targetType == float.class) return 0.0f;
            if (targetType == short.class) return (short) 0;
            if (targetType == byte.class) return (byte) 0;
            return null;
        }

        if (targetType.isInstance(rawValue)) {
            return rawValue;
        }

        if (targetType == Long.class || targetType == long.class) {
            if (rawValue instanceof Number) return ((Number) rawValue).longValue();
            return Long.parseLong(rawValue.toString());
        }

        if (targetType == Integer.class || targetType == int.class) {
            if (rawValue instanceof Number) return ((Number) rawValue).intValue();
            return Integer.parseInt(rawValue.toString());
        }

        if (targetType == Double.class || targetType == double.class) {
            if (rawValue instanceof Number) return ((Number) rawValue).doubleValue();
            return Double.parseDouble(rawValue.toString());
        }

        if (targetType == Float.class || targetType == float.class) {
            if (rawValue instanceof Number) return ((Number) rawValue).floatValue();
            return Float.parseFloat(rawValue.toString());
        }

        if (targetType == Short.class || targetType == short.class) {
            if (rawValue instanceof Number) return ((Number) rawValue).shortValue();
            return Short.parseShort(rawValue.toString());
        }

        if (targetType == Byte.class || targetType == byte.class) {
            if (rawValue instanceof Number) return ((Number) rawValue).byteValue();
            return Byte.parseByte(rawValue.toString());
        }

        if (targetType == Boolean.class || targetType == boolean.class) {
            if (rawValue instanceof Boolean) return rawValue;
            if (rawValue instanceof Number) return ((Number) rawValue).intValue() != 0;
            return Boolean.parseBoolean(rawValue.toString());
        }

        if (targetType == UUID.class) {
            if (rawValue instanceof UUID) return rawValue;
            return UUID.fromString(rawValue.toString());
        }

        if (targetType == BigDecimal.class) {
            if (rawValue instanceof BigDecimal) return rawValue;
            if (rawValue instanceof Number) return BigDecimal.valueOf(((Number) rawValue).doubleValue());
            return new BigDecimal(rawValue.toString());
        }

        if (targetType == BigInteger.class) {
            if (rawValue instanceof BigInteger) return rawValue;
            if (rawValue instanceof Number) return BigInteger.valueOf(((Number) rawValue).longValue());
            return new BigInteger(rawValue.toString());
        }

        if (targetType == LocalDateTime.class) {
            if (rawValue instanceof Timestamp) return ((Timestamp) rawValue).toLocalDateTime();
            if (rawValue instanceof java.util.Date) return new Timestamp(((java.util.Date) rawValue).getTime()).toLocalDateTime();
        }

        if (targetType == LocalDate.class) {
            if (rawValue instanceof java.sql.Date) return ((java.sql.Date) rawValue).toLocalDate();
            if (rawValue instanceof Timestamp) return ((Timestamp) rawValue).toLocalDateTime().toLocalDate();
        }

        if (targetType == LocalTime.class) {
            if (rawValue instanceof java.sql.Time) return ((java.sql.Time) rawValue).toLocalTime();
            if (rawValue instanceof Timestamp) return ((Timestamp) rawValue).toLocalDateTime().toLocalTime();
        }

        if (targetType == Instant.class) {
            if (rawValue instanceof Timestamp) return ((Timestamp) rawValue).toInstant();
            if (rawValue instanceof java.util.Date) return ((java.util.Date) rawValue).toInstant();
        }

        if (targetType == String.class) {
            return rawValue.toString();
        }

        return rawValue;
    }

    private static List<Object> loadJoinEntity(SelectJoinModel joinModel, Map<String, Object> values) {
        if (joinModel == null) return null;
        List<Object> entities = new ArrayList<>();
        try {
            for (Class<?> entityClass : joinModel.findJoinEntitiesClass()) {
                Constructor<?> constructor = entityClass.getDeclaredConstructor();
                constructor.setAccessible(true);
                Object entity = constructor.newInstance();
                for (Field field : SQLUtils.listFieldColumns(entityClass, false)) {
                    String columnName = joinModel.getTableReference(entityClass) + "_" + field.getName();
                    if (!values.containsKey(columnName)) continue;

                    field.setAccessible(true);
                    field.set(entity, convertValue(values.get(columnName), field.getType()));
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
