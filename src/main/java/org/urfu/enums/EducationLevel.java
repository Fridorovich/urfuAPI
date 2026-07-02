package org.urfu.enums;

import lombok.Getter;

@Getter
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

}