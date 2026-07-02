package org.urfu.enums;

public enum EducationStandard {
    SUOS("СУОС"),
    SUT("СУТ"),
    FGOS_VO("ФГОС ВО"),
    FGOS_VPO("ФГОС ВПО"),
    FGOS_3PP("ФГОС 3++");

    private final String displayName;

    EducationStandard(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}