package org.urfu.enums;

public enum EducationLevel {
    BACHELOR("Бакалавр"),
    APPLIED_BACHELOR("Прикладной бакалавриат"),
    SPECIALIST("Специалист"),
    MASTER("Магистр"),
    POSTGRADUATE("Аспирант");

    private final String displayName;

    EducationLevel(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}