package com.example.backend.db.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class QuestionType {
    private int id;
    private String name;
    private Type type;

    public QuestionType(int id, String name) {
        setId(id);
        setName(name);
        setType(name);
    }

    public QuestionType(Type type) {
        setName(type.toString());
        setType(type);
    }

    public boolean checkQuestionType(Type givenType) {
        return this.type == givenType;
    }

    private void setType(Type type) {
        this.type = type;
    }

    private void setType(String typeName) {
        switch (typeName) {
            case "OPEN":
                this.type = Type.OPEN;
                break;
            case "MULTIPLE_CHOICE":
                this.type = Type.MULTIPLE_CHOICE;
                break;
            case "TRUE_FALSE":
                this.type = Type.TRUE_FALSE;
                break;
            case "SHORT_ANSWER":
                this.type = Type.SHORT_ANSWER;
                break;
            case "ESSAY":
                this.type = Type.ESSAY;
                break;
            default:
                throw new IllegalArgumentException("Invalid question type: " + typeName);
        }
    }
}
