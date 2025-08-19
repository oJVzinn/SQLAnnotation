package com.github.ojvzinn.sqlannotation;

import com.github.ojvzinn.sqlannotation.entity.SQLConfigEntity;
import lombok.Getter;

public class SQLAnnotation {

    @Getter
    private static SQLConfigEntity config;

    public static void init(SQLConfigEntity entity) {
        entity.init();
        config = entity;
    }

    public static void destroy() {
        if (config != null) config.destroy();
    }
}
