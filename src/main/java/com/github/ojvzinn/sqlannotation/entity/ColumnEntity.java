package com.github.ojvzinn.sqlannotation.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ColumnEntity {

    private String name;
    private String type;
    private boolean notNull;
    private boolean autoIncrement;
    private boolean primaryKey;
    private int length;

    public boolean isVarchar() {
        return "VARCHAR".equalsIgnoreCase(type);
    }

    public String makeType() {
        return isVarchar() ? type + "(" + length + ")" : type;
    }
}
