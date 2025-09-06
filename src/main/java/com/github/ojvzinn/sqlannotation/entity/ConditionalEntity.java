package com.github.ojvzinn.sqlannotation.entity;

import com.github.ojvzinn.sqlannotation.enums.ConnectiveType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;

@RequiredArgsConstructor
@Getter
public class ConditionalEntity {

    @NonNull
    private ConnectiveType type;

    private final JSONObject conditions = new JSONObject();

    public ConditionalEntity appendConditional(String column, Object value) {
        conditions.put(column + " = ?", value);
        return this;
    }

}
