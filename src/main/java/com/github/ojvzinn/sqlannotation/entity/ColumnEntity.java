package com.github.ojvzinn.sqlannotation.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ColumnEntity {

    private String name;
    private String type;
    private boolean isNotNull;
    private boolean isAutoIncrement;

}
