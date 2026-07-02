package org.urfu.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.urfu.dto.ProgramCreateUpdateDTO;
import org.urfu.dto.ProgramDTO;
import org.urfu.entity.*;
import org.urfu.entity.Module;
import org.urfu.enums.EducationLevel;
import org.urfu.enums.EducationStandard;
import org.urfu.mapper.ProgramMapper;
import org.urfu.repository.*;
import jakarta.persistence.EntityNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProgramService {

    private final ProgramRepository programRepository;
    private final ModuleRepository moduleRepository;
    private final InstituteRepository instituteRepository;
    private final HeadRepository headRepository;
    private final ProgramMapper programMapper;

    @Transactional
    public ProgramDTO createProgram(ProgramCreateUpdateDTO dto) {
        log.info("Creating program: {}", dto.getTitle());

        Institute institute = instituteRepository.findById(dto.getInstituteUuid())
                .orElseThrow(() -> new EntityNotFoundException("Institute not found"));

        Head head = headRepository.findById(dto.getHeadUuid())
                .orElseThrow(() -> new EntityNotFoundException("Head not found"));

        Program program = Program.builder()
                .title(dto.getTitle())
                .cypher(dto.getCypher())
                .level(EducationLevel.valueOf(dto.getLevel()))
                .standard(EducationStandard.valueOf(dto.getStandard()))
                .institute(institute)
                .head(head)
                .accreditationDate(dto.getAccreditationDate())
                .modules(new ArrayList<>())
                .build();

        if (dto.getModuleUuids() != null && !dto.getModuleUuids().isEmpty()) {
            List<Module> modules = moduleRepository.findAllById(dto.getModuleUuids());
            program.setModules(modules);
        }

        Program saved = programRepository.save(program);
        return programMapper.toDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<ProgramDTO> getAllPrograms() {
        log.info("Getting all programs");
        List<Program> programs = programRepository.findAllWithModules();
        return programs.stream()
                .map(programMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProgramDTO getProgramById(UUID uuid) {
        log.info("Getting program by id: {}", uuid);
        Program program = programRepository.findByIdWithModules(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Program not found with id: " + uuid));
        return programMapper.toDTO(program);
    }

    @Transactional
    public ProgramDTO updateProgram(UUID uuid, ProgramCreateUpdateDTO dto) {
        log.info("Updating program: {}", uuid);

        Program program = programRepository.findById(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Program not found"));

        Institute institute = instituteRepository.findById(dto.getInstituteUuid())
                .orElseThrow(() -> new EntityNotFoundException("Institute not found"));

        Head head = headRepository.findById(dto.getHeadUuid())
                .orElseThrow(() -> new EntityNotFoundException("Head not found"));

        program.setTitle(dto.getTitle());
        program.setCypher(dto.getCypher());
        program.setLevel(EducationLevel.valueOf(dto.getLevel()));
        program.setStandard(EducationStandard.valueOf(dto.getStandard()));
        program.setInstitute(institute);
        program.setHead(head);
        program.setAccreditationDate(dto.getAccreditationDate());

        if (dto.getModuleUuids() != null) {
            List<Module> modules = moduleRepository.findAllById(dto.getModuleUuids());
            program.setModules(modules);
        }

        Program updated = programRepository.save(program);
        return programMapper.toDTO(updated);
    }

    @Transactional
    public void deleteProgram(UUID uuid) {
        log.info("Deleting program: {}", uuid);
        if (!programRepository.existsById(uuid)) {
            throw new EntityNotFoundException("Program not found");
        }
        programRepository.deleteById(uuid);
    }

    @Transactional(readOnly = true)
    public List<ProgramDTO> getProgramsWithModules() {
        log.info("Getting programs with modules");
        return getAllPrograms();
    }

    @Transactional(readOnly = true)
    public List<ProgramDTO> getSortedPrograms(String sortBy) {
        log.info("Getting programs sorted by: {}", sortBy);

        List<Program> programs = programRepository.findAllWithModules();
        List<Program> sorted = new ArrayList<>(programs);

        if (sortBy == null || sortBy.isEmpty()) {
            return sorted.stream()
                    .map(programMapper::toDTO)
                    .collect(Collectors.toList());
        }

        //Ручная сортировка, норм?
        Comparator<Program> comparator;
        switch (sortBy.toLowerCase()) {
            case "title":
                comparator = Comparator.comparing(Program::getTitle, String.CASE_INSENSITIVE_ORDER);
                break;
            case "cypher":
                comparator = Comparator.comparing(Program::getCypher, String.CASE_INSENSITIVE_ORDER);
                break;
            case "accreditationdate":
            case "accreditation":
                comparator = Comparator.comparing(Program::getAccreditationDate);
                break;
            case "standard":
                comparator = Comparator.comparing(p -> p.getStandard().name());
                break;
            default:
                throw new IllegalArgumentException("Invalid sort field: " + sortBy +
                        ". Allowed: title, cypher, accreditationDate, standard");
        }

        sorted.sort(comparator);

        return sorted.stream()
                .map(programMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProgramDTO addModuleToProgram(UUID programUuid, UUID moduleUuid) {
        Program program = programRepository.findById(programUuid)
                .orElseThrow(() -> new EntityNotFoundException("Program not found"));

        Module module = moduleRepository.findById(moduleUuid)
                .orElseThrow(() -> new EntityNotFoundException("Module not found"));

        if (program.getModules() == null) {
            program.setModules(new ArrayList<>());
        }

        if (!program.getModules().contains(module)) {
            program.getModules().add(module);
        }

        Program updated = programRepository.save(program);
        return programMapper.toDTO(updated);
    }

    @Transactional
    public ProgramDTO removeModuleFromProgram(UUID programUuid, UUID moduleUuid) {
        Program program = programRepository.findById(programUuid)
                .orElseThrow(() -> new EntityNotFoundException("Program not found"));

        if (program.getModules() != null) {
            program.getModules().removeIf(m -> m.getUuid().equals(moduleUuid));
        }

        Program updated = programRepository.save(program);
        return programMapper.toDTO(updated);
    }
}