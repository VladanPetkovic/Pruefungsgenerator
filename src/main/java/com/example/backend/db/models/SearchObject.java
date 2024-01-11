package com.example.backend.db.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchObject<T> {
    private String column_name;
    private T valueOfObject;
    private boolean isSet;

    public SearchObject(String column_name, T valueOfObject, boolean isSet) {
        setColumn_name(column_name);
        setValueOfObject(valueOfObject);
        setSet(isSet);
    }
}
