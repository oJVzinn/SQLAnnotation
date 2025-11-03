package com.github.ojvzinn.sqlannotation.model;

import com.github.ojvzinn.sqlannotation.SQL;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
public class SQLConfigModel {

    @NonNull
    private SQL SQLDataBase;

    private final HikariModel hikariConfig = new HikariModel();

    @Setter
    private boolean log = false;

    public void init() {
        SQLDataBase.init(this.hikariConfig);
    }

    public void destroy() {
        SQLDataBase.destroy();
    }
}
