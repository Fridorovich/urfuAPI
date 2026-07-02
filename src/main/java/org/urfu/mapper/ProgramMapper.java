package org.urfu.mapper;

import org.springframework.stereotype.Component;
import org.urfu.dto.*;
import org.urfu.entity.*;
import org.urfu.entity.Module;

import java.util.stream.Collectors;

@Component
public class ProgramMapper {

    public ProgramDTO toDTO(Program program) {
        return ProgramDTO.builder()
                .uuid(program.getUuid())
                .title(program.getTitle())
                .cypher(program.getCypher())
                .level(program.getLevel() != null ? program.getLevel().name() : null)
                .standard(program.getStandard() != null ? program.getStandard().name() : null)
                .institute(toInstituteDTO(program.getInstitute()))
                .head(toHeadDTO(program.getHead()))
                .accreditationDate(program.getAccreditationDate())
                .modules(program.getModules() != null
                        ? program.getModules().stream().map(this::toModuleDTO).collect(Collectors.toList())
                        : null)
                .build();
    }

    public InstituteDTO toInstituteDTO(Institute institute) {
        if (institute == null) return null;
        return InstituteDTO.builder()
                .uuid(institute.getUuid())
                .title(institute.getTitle())
                .build();
    }

    public HeadDTO toHeadDTO(Head head) {
        if (head == null) return null;
        return HeadDTO.builder()
                .uuid(head.getUuid())
                .fullname(head.getFullname())
                .build();
    }

    public ModuleDTO toModuleDTO(Module module) {
        if (module == null) return null;
        return ModuleDTO.builder()
                .uuid(module.getUuid())
                .title(module.getTitle())
                .type(module.getType() != null ? module.getType().name() : null)
                .build();
    }
}