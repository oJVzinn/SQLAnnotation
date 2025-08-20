package com.github.ojvzinn.sqlannotation.logger;

import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

public class SQLogger extends Logger {

    public SQLogger(String name) {
        super(name, null);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new SQLFormatter(name));
        addHandler(handler);
        setUseParentHandlers(false);
    }
}
