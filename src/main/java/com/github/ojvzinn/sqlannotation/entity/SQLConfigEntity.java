package com.github.ojvzinn.sqlannotation.entity;

import com.github.ojvzinn.sqlannotation.SQL;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SQLConfigEntity {

    @NonNull
    private SQL SQLDataBase;

    @Getter
    private HikariEntity hikariConfig = new HikariEntity();

    public void init() {
        SQLDataBase.init(this.hikariConfig);
    }

    public void destroy() {
        SQLDataBase.destroy();
    }
}
