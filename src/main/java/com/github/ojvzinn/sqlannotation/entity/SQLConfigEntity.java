package com.github.ojvzinn.sqlannotation.entity;

import com.github.ojvzinn.sqlannotation.SQL;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
public class SQLConfigEntity {

    @NonNull
    private SQL SQLDataBase;

    private final HikariEntity hikariConfig = new HikariEntity();

    @Setter
    private boolean log = false;

    public void init() {
        SQLDataBase.init(this.hikariConfig);
    }

    public void destroy() {
        SQLDataBase.destroy();
    }
}
