package org.urfu.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.urfu.dto.ModuleCreateUpdateDTO;
import org.urfu.dto.ModuleDTO;
import org.urfu.entity.Module;
import org.urfu.enums.ModuleType;
import org.urfu.mapper.ProgramMapper;
import org.urfu.repository.ModuleRepository;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Сервис для управления модулями.
 * Предоставляет полный CRUD функционал для работы с модулями образовательных программ.
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ModuleService {

    private final ModuleRepository moduleRepository;
    private final ProgramMapper programMapper;

    /**
     * Создает новый модуль.
     * Перед созданием проверяет, что модуль с таким названием не существует.
     * Тип модуля должен соответствовать одному из перечисленных в enum ModuleType.
     *
     * @param dto данные для создания модуля
     * @return созданный модуль в виде DTO
     * @throws IllegalArgumentException если модуль с таким названием уже существует
     * @throws IllegalArgumentException если указан некорректный тип модуля
     */
    @Transactional
    public ModuleDTO createModule(ModuleCreateUpdateDTO dto) {
        log.info("Creating module: {}", dto.getTitle());

        if (moduleRepository.existsByTitle(dto.getTitle())) {
            throw new IllegalArgumentException("Module with title '" + dto.getTitle() + "' already exists");
        }

        Module module = Module.builder()
                .title(dto.getTitle())
                .type(ModuleType.valueOf(dto.getType()))
                .build();

        Module saved = moduleRepository.save(module);
        return programMapper.toModuleDTO(saved);
    }

    /**
     * Получает список всех модулей.
     *
     * @return список всех модулей в виде DTO
     */
    @Transactional(readOnly = true)
    public List<ModuleDTO> getAllModules() {
        log.info("Getting all modules");
        return moduleRepository.findAll().stream()
                .map(programMapper::toModuleDTO)
                .collect(Collectors.toList());
    }

    /**
     * Получает модуль по его UUID.
     *
     * @param uuid UUID модуля
     * @return модуль в виде DTO
     * @throws EntityNotFoundException если модуль с указанным UUID не найден
     */
    @Transactional(readOnly = true)
    public ModuleDTO getModuleById(UUID uuid) {
        log.info("Getting module by id: {}", uuid);
        Module module = moduleRepository.findById(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Module not found with id: " + uuid));
        return programMapper.toModuleDTO(module);
    }

    /**
     * Обновляет данные существующего модуля.
     * Если изменяется название, проверяет, что новое название не занято другим модулем.
     * Тип модуля должен соответствовать одному из перечисленных в enum ModuleType.
     *
     * @param uuid UUID обновляемого модуля
     * @param dto новые данные для модуля
     * @return обновленный модуль в виде DTO
     * @throws EntityNotFoundException если модуль с указанным UUID не найден
     * @throws IllegalArgumentException если новое название уже используется другим модулем
     * @throws IllegalArgumentException если указан некорректный тип модуля
     */
    @Transactional
    public ModuleDTO updateModule(UUID uuid, ModuleCreateUpdateDTO dto) {
        log.info("Updating module: {}", uuid);

        Module module = moduleRepository.findById(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Module not found with id: " + uuid));

        if (!module.getTitle().equals(dto.getTitle()) &&
                moduleRepository.existsByTitle(dto.getTitle())) {
            throw new IllegalArgumentException("Module with title '" + dto.getTitle() + "' already exists");
        }

        module.setTitle(dto.getTitle());
        module.setType(ModuleType.valueOf(dto.getType()));

        Module updated = moduleRepository.save(module);
        return programMapper.toModuleDTO(updated);
    }

    /**
     * Удаляет модуль по его UUID.
     * Удаление возможно только если модуль не используется в программах.
     *
     * @param uuid UUID удаляемого модуля
     * @throws EntityNotFoundException если модуль с указанным UUID не найден
     */
    @Transactional
    public void deleteModule(UUID uuid) {
        log.info("Deleting module: {}", uuid);

        if (!moduleRepository.existsById(uuid)) {
            throw new EntityNotFoundException("Module not found with id: " + uuid);
        }

        moduleRepository.deleteById(uuid);
    }
}