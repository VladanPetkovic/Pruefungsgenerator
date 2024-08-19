package com.example.application.backend.app;

public enum SortType {
    POINTS,
    DIFFICULTY,
    CREATED_AT,
    UPDATED_AT;

    public static boolean checkType(String sortTypeName) {
        if (sortTypeName == null) {
            return false;
        }

        for (SortType type : SortType.values()) {
            if (type.name().equalsIgnoreCase(sortTypeName)) {
                return true;
            }
        }
        return false;
    }

    public static String getSortTypeLowercase(SortType sortType) {
        return switch (sortType) {
            case DIFFICULTY -> "difficulty";
            case CREATED_AT -> "createdAt";
            case UPDATED_AT -> "updatedAt";
            default -> "points";
        };
    }
}
