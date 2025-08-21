package com.github.ojvzinn.sqlannotation;

import com.github.ojvzinn.sqlannotation.entity.SQLConfigEntity;
import lombok.Getter;

import java.util.Map;

public class SQLAnnotation {

    @Getter
    private static SQLConfigEntity config;

    public static void scanTable(Class<?> classTable) {
        config.getSQLDataBase().scanTable(classTable);
    }

    public static void dropTable(Class<?> classTable) {
        config.getSQLDataBase().dropTable(classTable);
    }

    public static void checkColumn(Class<?> classTable, String columnField) {
        config.getSQLDataBase().checkColumn(classTable, columnField);
    }

    public static <T> T findByConditionals(Class<?> classTable, Map<String, Object> conditonals) {
        return (T) config.getSQLDataBase().findByConditionals(classTable, conditonals);
    }

    public static <T> T findByKey(Class<?> classTable, Object key) {
        return (T) config.getSQLDataBase().findByKey(classTable, key);
    }

    public static void init(SQLConfigEntity entity) {
        entity.init();
        config = entity;
    }

    public static void destroy() {
        if (config != null) config.destroy();
    }
}
