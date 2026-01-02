package com.github.ojvzinn.sqlannotation.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SQLTimerModel {

    private Long start;

    public Long stop() {
        return System.currentTimeMillis() - start;
    }

}
