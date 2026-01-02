package com.github.ojvzinn.sqlannotation.model;

import com.github.ojvzinn.sqlannotation.annotations.Entity;
import com.github.ojvzinn.sqlannotation.annotations.Join;
import com.github.ojvzinn.sqlannotation.utils.SQLUtils;
import lombok.AllArgsConstructor;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class SelectJoinModel {

    private Class<?> entityClass;

    public StringBuilder generateSelectQuery() {
        StringBuilder sql = new StringBuilder("SELECT ");
        addColumns(sql, getTableReference(entityClass), SQLUtils.listFieldColumns(entityClass, false));
        sql.append(", ");
        List<Class<?>> joinEntities = findJoinEntitiesClass();
        for (int i = 0; i < joinEntities.size(); i++) {
            Class<?> entityClass = joinEntities.get(i);
            addColumns(sql, getTableReference(entityClass),
                    SQLUtils.listFieldColumns(entityClass, false));
            if (i != joinEntities.size() - 1) sql.append(", ");
        }

        return sql;
    }

    public String makeJoinQuery() {
        StringBuilder finalJoin = new StringBuilder();
        List<Class<?>> joinEntities = findJoinEntitiesClass();
        for (int i = 0; i < joinEntities.size(); i++) {
            Class<?> entityClass = joinEntities.get(i);
            Field entityField = findFieldByType(entityClass);
            finalJoin.append("JOIN ")
                    .append(entityClass.getAnnotation(Entity.class).name())
                    .append(" ON ")
                    .append(getEntityTableReference())
                    .append(".")
                    .append(entityField.getName())
                    .append(" = ")
                    .append(getTableReference(entityClass))
                    .append(".")
                    .append(entityField.getAnnotation(Join.class).column());
            if (i < joinEntities.size() - 1) finalJoin.append(" ");
        }

        return finalJoin.toString();
    }

    public String getEntityTableReference() {
        return getTableReference(entityClass);
    }

    public String getTableReference(Class<?> entityClass) {
        return entityClass.getAnnotation(Entity.class).name().toLowerCase();
    }

    public List<Class<?>> findJoinEntitiesClass() {
        return listJoinFields().stream().map(Field::getType).collect(Collectors.toList());
    }

    private List<Field> listJoinFields() {
        return Arrays.stream(entityClass.getDeclaredFields()).filter(field -> field.getAnnotation(Join.class) != null).collect(Collectors.toList());
    }

    private Field findFieldByType(Class<?> type) {
        return Arrays.stream(entityClass.getDeclaredFields()).filter(field -> field.getType().equals(type)).findFirst().orElse(null);
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
