package com.github.ojvzinn.sqlannotation.enums;

import lombok.Getter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.UUID;

@Getter
public enum ClassType {

    INT("INT", Integer.class, int.class),
    BIGINT("BIGINT", Long.class, long.class, BigInteger.class),
    SMALLINT("SMALLINT", Short.class, short.class),
    TINYINT("TINYINT", Byte.class, byte.class),
    DOUBLE("DOUBLE", Double.class, double.class),
    FLOAT("FLOAT", Float.class, float.class),
    BOOLEAN("BOOLEAN", Boolean.class, boolean.class),
    DECIMAL("DECIMAL(19, 4)", BigDecimal.class),
    VARCHAR("VARCHAR", UUID.class),
    TEXT("TEXT", String.class),
    BLOB("BLOB", byte[].class, Byte[].class),
    DATETIME("DATETIME", Timestamp.class, LocalDateTime.class, Instant.class),
    DATE("DATE", Date.class, LocalDate.class),
    TIME("TIME", Time.class, LocalTime.class);

    private final String type;
    private final Class<?>[] supportedClasses;

    ClassType(String type, Class<?>... supportedClasses) {
        this.type = type;
        this.supportedClasses = supportedClasses;
    }

    public static ClassType getType(Class<?> classType, boolean isVarchar) {
        if (classType == String.class) {
            return isVarchar ? VARCHAR : TEXT;
        }

        return Arrays.stream(values())
                .filter(t -> Arrays.stream(t.supportedClasses).anyMatch(c -> c.equals(classType) || c.isAssignableFrom(classType)))
                .findFirst()
                .orElse(null);
    }

    public static ClassType getType(Class<?> classType) {
        return getType(classType, false);
    }
}
