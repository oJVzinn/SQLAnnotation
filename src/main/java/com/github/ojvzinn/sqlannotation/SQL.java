package com.github.ojvzinn.sqlannotation;

import com.github.ojvzinn.sqlannotation.model.HikariModel;
import com.github.ojvzinn.sqlannotation.modules.*;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;

@Setter
@Getter
public abstract class SQL {

    private HikariDataSource dataSource = null;
    private CreateModule createModule = new CreateModule(this);
    private DeleteModule deleteModule = new DeleteModule(this);
    private InsertModule insertModule = new InsertModule(this);
    private SelectModule selectModule = new SelectModule(this);
    private UpdateModule updateModule = new UpdateModule(this);

    public abstract void init(HikariModel model);

    public abstract void destroy();

    public abstract String makeSQLCreateTable(String table, LinkedHashMap<String, Object> columns);

    public abstract String makeSQLCheckColumn(String table, LinkedHashMap<String, Object> column);

}
