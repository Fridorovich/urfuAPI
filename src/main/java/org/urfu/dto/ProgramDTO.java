package org.urfu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgramDTO {
    private UUID uuid;
    private String title;
    private String cypher;
    private String level;
    private String standard;
    private InstituteDTO institute;
    private HeadDTO head;
    private LocalDate accreditationDate;
    private List<ModuleDTO> modules;
}