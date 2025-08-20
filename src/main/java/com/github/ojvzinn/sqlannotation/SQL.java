package com.github.ojvzinn.sqlannotation;

import com.github.ojvzinn.sqlannotation.annotations.Column;
import com.github.ojvzinn.sqlannotation.annotations.Table;
import com.github.ojvzinn.sqlannotation.entity.ColumnEntity;
import com.github.ojvzinn.sqlannotation.entity.HikariEntity;
import com.github.ojvzinn.sqlannotation.utils.SQLUtils;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public abstract class SQL {

    @Setter
    private HikariDataSource dataSource = null;

    public void scanTable(Class<?> tableClass) {
        Table tableName = tableClass.getAnnotation(Table.class);
        if (tableName == null) {
            throw new RuntimeException("The table class needs to come with the @Table annotation");
        }

        String name = tableName.name();
        Map<String, ColumnEntity> columns = new HashMap<>();
        Field[] fields = tableClass.getDeclaredFields();
        if (fields.length == 0) {
            throw new RuntimeException("To create a table it is necessary to have at least one column field");
        }

        for (Field field : Arrays.stream(fields).filter(field -> field.getAnnotation(Column.class) != null).collect(Collectors.toList())) {
            String columnName = field.getName();
            String type = SQLUtils.getType(field.getType());
        }
    }

    public abstract void init(HikariEntity entity);
    public abstract void destroy();

}
