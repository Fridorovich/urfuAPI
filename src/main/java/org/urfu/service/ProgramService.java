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

/**
 * Сервис для управления образовательными программами.
 * Предоставляет полный CRUD функционал, а также дополнительные методы
 * для работы с модулями и сортировки программ.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProgramService {

    private final ProgramRepository programRepository;
    private final ModuleRepository moduleRepository;
    private final InstituteRepository instituteRepository;
    private final HeadRepository headRepository;
    private final ProgramMapper programMapper;

    /**
     * Создает новую образовательную программу.
     * Проверяет существование института и ответственного лица.
     * Привязывает указанные модули к программе.
     *
     * @param dto данные для создания программы
     * @return созданная программа в виде DTO
     * @throws EntityNotFoundException если институт или ответственное лицо не найдены
     */
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

    /**
     * Получает список всех образовательных программ.
     * Программы загружаются со всеми связанными сущностями.
     *
     * @return список всех программ в виде DTO
     */
    @Transactional(readOnly = true)
    public List<ProgramDTO> getAllPrograms() {
        log.info("Getting all programs");
        List<Program> programs = programRepository.findAllWithModules();
        return programs.stream()
                .map(programMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Получает программу по её UUID.
     * Программа загружается со всеми связанными сущностями.
     *
     * @param uuid UUID программы
     * @return программа в виде DTO
     * @throws EntityNotFoundException если программа не найдена
     */
    @Transactional(readOnly = true)
    public ProgramDTO getProgramById(UUID uuid) {
        log.info("Getting program by id: {}", uuid);
        Program program = programRepository.findByIdWithModules(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Program not found with id: " + uuid));
        return programMapper.toDTO(program);
    }

    /**
     * Обновляет существующую образовательную программу.
     * Проверяет существование всех связанных сущностей.
     * Обновляет список модулей программы.
     *
     * @param uuid UUID обновляемой программы
     * @param dto  новые данные для программы
     * @return обновленная программа в виде DTO
     * @throws EntityNotFoundException если программа, институт или ответственное лицо не найдены
     */
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

    /**
     * Удаляет образовательную программу по её UUID.
     *
     * @param uuid UUID удаляемой программы
     * @throws EntityNotFoundException если программа не найдена
     */
    @Transactional
    public void deleteProgram(UUID uuid) {
        log.info("Deleting program: {}", uuid);
        if (!programRepository.existsById(uuid)) {
            throw new EntityNotFoundException("Program not found");
        }
        programRepository.deleteById(uuid);
    }

    /**
     * Получает список всех программ с модулями.
     * Аналогичен методу getAllPrograms().
     *
     * @return список программ с модулями
     */
    @Transactional(readOnly = true)
    public List<ProgramDTO> getProgramsWithModules() {
        log.info("Getting programs with modules");
        return getAllPrograms();
    }

    /**
     * Получает отсортированный список программ.
     * Реализована ручная сортировка на уровне сервиса.
     *
     * @param sortBy поле для сортировки
     * @return отсортированный список программ
     * @throws IllegalArgumentException если указано некорректное поле для сортировки
     */
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

        Comparator<Program> comparator = switch (sortBy.toLowerCase()) {
            case "title" -> Comparator.comparing(Program::getTitle, String.CASE_INSENSITIVE_ORDER);
            case "cypher" -> Comparator.comparing(Program::getCypher, String.CASE_INSENSITIVE_ORDER);
            case "accreditationdate", "accreditation" -> Comparator.comparing(Program::getAccreditationDate);
            case "standard" -> Comparator.comparing(p -> p.getStandard().name());
            default -> throw new IllegalArgumentException("Invalid sort field: " + sortBy +
                    ". Allowed: title, cypher, accreditationDate, standard");
        };

        sorted.sort(comparator);

        return sorted.stream()
                .map(programMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Добавляет существующий модуль к программе.
     * Проверяет, что модуль еще не привязан к программе.
     *
     * @param programUuid UUID программы
     * @param moduleUuid  UUID добавляемого модуля
     * @return обновленная программа с новым модулем
     * @throws EntityNotFoundException если программа или модуль не найдены
     */
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

    /**
     * Удаляет модуль из программы.
     * Модуль не удаляется из системы, только отвязывается от программы.
     *
     * @param programUuid UUID программы
     * @param moduleUuid  UUID удаляемого модуля
     * @return обновленная программа без удаленного модуля
     * @throws EntityNotFoundException если программа не найдена
     */
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