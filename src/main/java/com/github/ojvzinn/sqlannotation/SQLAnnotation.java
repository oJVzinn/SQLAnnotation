package com.github.ojvzinn.sqlannotation;

import com.github.ojvzinn.sqlannotation.entity.SQLConfigEntity;
import com.github.ojvzinn.sqlannotation.interfaces.Repository;
import com.github.ojvzinn.sqlannotation.processor.RepositoryProcessor;
import lombok.Getter;

public class SQLAnnotation {

    @Getter
    private static SQLConfigEntity config;

    private static final RepositoryProcessor processor = new RepositoryProcessor();

    public static <T extends Repository> T loadRepository(Class<T> repository) {
        return repository.cast(processor.processRepository(repository));
    }

    public static void scanEntity(Class<?> entity) {
        config.getSQLDataBase().getCreateModule().scanEntity(entity);
    }

    public static void drop(Class<?> entity) {
        config.getSQLDataBase().getDeleteModule().drop(entity);
    }

    public static void init(SQLConfigEntity entity) {
        entity.init();
        config = entity;
    }

    public static void destroy() {
        if (config != null) config.destroy();
    }
}
