package org.urfu.enums;

public enum ModuleType {
    STANDARD("Стандартный модуль"),
    PROJECT_EDUCATION("Модуль проектного обучения"),
    MINOR("Модуль майора"),
    SECTION_FK("Модуль секции ФК"),
    FOREIGN_LANGUAGE("Модуль ин.яз.");

    private final String displayName;

    ModuleType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}