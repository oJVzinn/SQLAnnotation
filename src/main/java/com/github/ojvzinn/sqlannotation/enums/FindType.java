package com.github.ojvzinn.sqlannotation.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum FindType {

    BY_CONDITIONALS("AllByConditionals"),
    BY_KEY("By"),
    ALL("All");

    private final String type;

    public static FindType findByType(String type) {
        return Arrays.stream(values()).filter(findType -> type.startsWith(findType.type)).findFirst().orElse(null);
    }
}
