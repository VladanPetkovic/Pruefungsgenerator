package com.example.application.backend.db.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "question_types")
public class QuestionType implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Transient
    private Type type;

    public QuestionType(Long id, String name) {
        setId(id);
        setName(name);
        setType(name);
    }

    public QuestionType(Type type) {
        setName(type.toString());
        setType(type);
    }

    public QuestionType(String typeString) {
        setType(typeString);
        if (getType() != null) {
            setName(getType().toString());
        }
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

    /**
     * This function is used to check whether the questionType was set or not.
     *
     * @param typeName The string we are reading from the MenuButton
     * @return A Boolean, true, if the typeName matches any enum Type; false otherwise.
     */
    public static boolean checkExistingType(String typeName) {
        if (typeName == null) {
            return false;
        }

        for (Type type : Type.values()) {
            if (type.name().equalsIgnoreCase(typeName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkMultipleChoiceType(String typeName) {
        if (typeName == null) {
            return false;
        }

        if (Type.MULTIPLE_CHOICE.name().equals(typeName)) {
            return true;
        }
        return false;
    }
}
