package com.github.ojvzinn.sqlannotation.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ColumnModel {

    private String name;
    private String type;
    private boolean notNull;
    private boolean autoIncrement;
    private boolean primaryKey;
    private boolean unique;
    private int length;

    public boolean isVarchar() {
        return "VARCHAR".equalsIgnoreCase(type);
    }

    public String makeType() {
        return isVarchar() ? type + "(" + length + ")" : type;
    }
}
