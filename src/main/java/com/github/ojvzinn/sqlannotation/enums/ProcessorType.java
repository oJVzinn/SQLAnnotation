package com.github.ojvzinn.sqlannotation.enums;

import com.github.ojvzinn.sqlannotation.interfaces.Processor;
import com.github.ojvzinn.sqlannotation.processor.ProcessorFind;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
public enum ProcessorType {

    FIND_ENTITY("find", new ProcessorFind());

    private final String processorKey;

    @Getter
    private final Processor processor;

    public static ProcessorType findByKey(String key) {
        return Arrays.stream(values()).filter(type -> key.startsWith(type.processorKey)).findFirst().orElse(null);
    }

}
