package com.github.ojvzinn.sqlannotation;

import com.github.ojvzinn.sqlannotation.annotations.Table;
import com.github.ojvzinn.sqlannotation.entity.HikariEntity;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.Setter;

@Getter
public abstract class SQL {

    @Setter
    private HikariDataSource dataSource = null;

    public void scanTable(Class<?> tableClass) {
        if (tableClass == null || tableClass.getAnnotation(Table.class) == null) {
            throw new RuntimeException("The table class needs to come with the @Table annotation");
        }
    }

    public abstract void init(HikariEntity entity);
    public abstract void destroy();

}
