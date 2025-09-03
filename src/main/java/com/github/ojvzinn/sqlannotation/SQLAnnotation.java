package com.github.ojvzinn.sqlannotation;

import com.github.ojvzinn.sqlannotation.entity.SQLConfigEntity;
import com.github.ojvzinn.sqlannotation.interfaces.Repository;
import lombok.Getter;

public class SQLAnnotation {

    @Getter
    private static SQLConfigEntity config;

    public static <T extends Repository> T loadRepository(Class<T> repository) {
        return (T) RepositoryProcessor.processRepository(repository);
    }

    public static void scanEntity(Class<?> entity) {
        config.getSQLDataBase().getCreateModule().scanEntity(entity);
    }

    public static void dropEntityTable(Class<?> entity) {
        config.getSQLDataBase().getDropModule().dropTable(entity);
    }

    public static void init(SQLConfigEntity entity) {
        entity.init();
        config = entity;
    }

    public static void destroy() {
        if (config != null) config.destroy();
    }
}
