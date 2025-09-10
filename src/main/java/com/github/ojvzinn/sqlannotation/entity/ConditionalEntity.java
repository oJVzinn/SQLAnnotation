package com.github.ojvzinn.sqlannotation.entity;

import com.github.ojvzinn.sqlannotation.enums.ConnectiveType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;

import java.util.Set;

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

    public String build() {
        StringBuilder sql = new StringBuilder();
        Set<String> keys = getConditions().keySet();
        int i = 0;
        for (String conditional : getConditions().keySet()) {
            sql.append(" ").append(conditional);
            if (i + 1 != keys.size()) sql.append(" ").append(getType().name());
            i++;
        }

        return sql.toString();
    }

}
