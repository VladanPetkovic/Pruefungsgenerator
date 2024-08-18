package com.example.application.backend.db.models;

public enum Type {
    OPEN,
    MULTIPLE_CHOICE,
    TRUE_FALSE;

    /**
     * This function is used to check whether the questionType was set or not.
     *
     * @param typeName The string we are reading from the MenuButton
     * @return A Boolean, true, if the typeName matches any enum Type; false otherwise.
     */
    public static boolean checkType(String typeName) {
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

    public static boolean isMultipleChoice(String typeName) {
        return Type.MULTIPLE_CHOICE.name().equals(typeName);
    }

    public static boolean isTrueFalse(String typeName) {
        return Type.TRUE_FALSE.name().equals(typeName);
    }

    public static boolean isOpen(String typeName) {
        return Type.OPEN.name().equals(typeName);
    }

    public static String getType(Type type) {
        if (type == null) {
            return null;
        }
        return type.name();
    }

    public static Type getType(String typeName) {
        if (typeName == null) {
            return null;
        }

        for (Type type : Type.values()) {
            if (type.name().equalsIgnoreCase(typeName)) {
                return type;
            }
        }
        return null;
    }
}
