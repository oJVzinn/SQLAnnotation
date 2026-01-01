package com.github.ojvzinn.sqlannotation.model;

import com.github.ojvzinn.sqlannotation.annotations.Entity;
import com.github.ojvzinn.sqlannotation.annotations.Join;
import com.github.ojvzinn.sqlannotation.utils.SQLUtils;
import lombok.AllArgsConstructor;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public class SelectJoinModel {

    private Class<?> entityClass;

    public StringBuilder generateSelectQuery() {
        StringBuilder sql = new StringBuilder("SELECT ");
        addColumns(sql, getTableReference(), SQLUtils.listFieldColumns(entityClass));
        sql.append(", ");
        addColumns(sql, getJoinTableReference(), SQLUtils.listFieldColumns(getJoinField().getType()));
        return sql;
    }

    public String makeJoinQuery() {
        return "JOIN " +
                findJoinEntityClass().getAnnotation(Entity.class).name() +
                " AS " +
                getJoinTableReference() +
                " ON " +
                getTableReference() +
                "." +
                getColumnReference() +
                " = " +
                getJoinTableReference() +
                "." +
                getJoinColumnReference();
    }

    public String getTableReference() {
        return entityClass.getAnnotation(Entity.class).name().toLowerCase();
    }

    public String getJoinTableReference() {
        return findJoinEntityClass().getAnnotation(Entity.class).name().toLowerCase();
    }

    public Class<?> findJoinEntityClass() {
        return Arrays.stream(entityClass.getDeclaredFields())
                .map(Field::getType)
                .filter(fieldClass -> fieldClass.getAnnotation(Entity.class) != null)
                .findFirst().orElse(null);
    }

    private String getJoinColumnReference() {
        Field joinEntityField = getJoinField();
        if (joinEntityField == null) {
            throw new IllegalArgumentException("Join entity class doesn't exist");
        }

        return joinEntityField.getAnnotation(Join.class).column();
    }

    private String getColumnReference() {
        Field joinEntityField = getJoinField();
        if (joinEntityField == null) {
            throw new IllegalArgumentException("Join entity class doesn't exist");
        }

        return joinEntityField.getName();
    }

    private Field getJoinField() {
        return Arrays.stream(entityClass.getDeclaredFields()).filter(field -> field.getType().equals(findJoinEntityClass())).findFirst().orElse(null);
    }

    private void addColumns(StringBuilder sql, String reference, List<Field> fields) {
        int i = 0;
        for (Field field : fields) {
            sql.append(reference)
                    .append(".")
                    .append(field.getName())
                    .append(" AS ")
                    .append(reference)
                    .append("_")
                    .append(field.getName());

            if (i + 1 != fields.size()) sql.append(", ");
            i++;
        }
    }
}
