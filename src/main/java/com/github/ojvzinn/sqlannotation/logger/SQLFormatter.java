package com.github.ojvzinn.sqlannotation.logger;

import lombok.AllArgsConstructor;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

@AllArgsConstructor
public class SQLFormatter extends Formatter {

    private String module;

    @Override
    public String format(LogRecord record) {
        return "[" + module + "] " + record.getMessage() + "\n";
    }

}
