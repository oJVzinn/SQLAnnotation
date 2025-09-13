package com.github.ojvzinn.sqlannotation.entity;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SQLTimerEntity {

    private Long start;

    public Long stop() {
        return System.currentTimeMillis() - start;
    }

}
