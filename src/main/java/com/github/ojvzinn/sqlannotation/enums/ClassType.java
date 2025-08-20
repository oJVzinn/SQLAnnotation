package com.github.ojvzinn.sqlannotation.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum ClassType {

    INT(Integer.class),
    TEXT(String.class),
    VARCHAR(String.class),
    BIGINT(Long.class);

    private final Class<?> classType;

    public String getType() {
        return name().toUpperCase();
    }

    public static ClassType getType(Class<?> classType) {
        return Arrays.stream(values()).filter(t -> t.getClassType() == classType).findFirst().orElse(null);
    }
}
