package com.github.ojvzinn.sqlannotation.model;

import com.github.ojvzinn.sqlannotation.annotations.Entity;
import com.github.ojvzinn.sqlannotation.annotations.Join;
import lombok.AllArgsConstructor;

import java.lang.reflect.Field;
import java.util.Arrays;

@AllArgsConstructor
public class SelectJoinModel {

    private Class<?> entityClass;

    public String makeJoinQuery() {
        return "JOIN " +
                findEntityClass().getName() +
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
        return findEntityClass().getAnnotation(Entity.class).name().toLowerCase();
    }

    private Class<?> findEntityClass() {
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
        return Arrays.stream(entityClass.getDeclaredFields()).filter(field -> field.getDeclaringClass().equals(findEntityClass())).findFirst().orElse(null);
    }
}
