package org.urfu.enums;

import lombok.Getter;

@Getter
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

}