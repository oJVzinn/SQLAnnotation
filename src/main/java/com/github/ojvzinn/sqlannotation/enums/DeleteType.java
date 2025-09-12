package com.github.ojvzinn.sqlannotation.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum DeleteType {

    ROWS("Rows"),
    BY_CONDITIONALS("AllByConditionals"),
    BY_KEY("By"),
    ALL("All");

    private final String type;

    public static DeleteType findByType(String type) {
        return Arrays.stream(values()).filter(findType -> type.startsWith(findType.type)).findFirst().orElse(null);
    }
}
