package com.github.ojvzinn.sqlannotation.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@AllArgsConstructor
@Getter
public class LimitModel {

    private int limit;

    public String build() {
        return "LIMIT " + limit;
    }
}
