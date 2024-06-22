package com.example.backend.db.models;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class SearchObject<T> implements Serializable {
    private String column_name;
    private String objectName;
    private T valueOfObject;
    private boolean isSet;

    public SearchObject(String column_name, String objectName, T valueOfObject, boolean isSet) {
        setColumn_name(column_name);
        setObjectName(objectName);
        setValueOfObject(valueOfObject);
        setSet(isSet);
    }

    // needed for createAutomaticTest
    public SearchObject() {

    }
}
