package org.urfu.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.urfu.dto.InstituteCreateUpdateDTO;
import org.urfu.dto.InstituteDTO;
import org.urfu.entity.Institute;
import org.urfu.mapper.ProgramMapper;
import org.urfu.repository.InstituteRepository;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Сервис для управления институтами.
 * Предоставляет полный CRUD функционал для работы с институтами УрФУ.
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InstituteService {

    private final InstituteRepository instituteRepository;
    private final ProgramMapper programMapper;

    /**
     * Создает новый институт.
     * Перед созданием проверяет, что институт с таким названием не существует.
     *
     * @param dto данные для создания института
     * @return созданный институт в виде DTO
     * @throws IllegalArgumentException если институт с таким названием уже существует
     */
    @Transactional
    public InstituteDTO createInstitute(InstituteCreateUpdateDTO dto) {
        log.info("Creating institute: {}", dto.getTitle());

        if (instituteRepository.existsByTitle(dto.getTitle())) {
            throw new IllegalArgumentException("Institute with title '" + dto.getTitle() + "' already exists");
        }

        Institute institute = Institute.builder()
                .title(dto.getTitle())
                .build();

        Institute saved = instituteRepository.save(institute);
        return programMapper.toInstituteDTO(saved);
    }

    /**
     * Получает список всех институтов.
     *
     * @return список всех институтов в виде DTO
     */
    @Transactional(readOnly = true)
    public List<InstituteDTO> getAllInstitutes() {
        log.info("Getting all institutes");
        return instituteRepository.findAll().stream()
                .map(programMapper::toInstituteDTO)
                .collect(Collectors.toList());
    }

    /**
     * Получает институт по его UUID.
     *
     * @param uuid UUID института
     * @return институт в виде DTO
     * @throws EntityNotFoundException если институт с указанным UUID не найден
     */
    @Transactional(readOnly = true)
    public InstituteDTO getInstituteById(UUID uuid) {
        log.info("Getting institute by id: {}", uuid);
        Institute institute = instituteRepository.findById(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Institute not found with id: " + uuid));
        return programMapper.toInstituteDTO(institute);
    }

    /**
     * Обновляет данные существующего института.
     * Если изменяется название, проверяет, что новое название не занято другим институтом.
     *
     * @param uuid UUID обновляемого института
     * @param dto новые данные для института
     * @return обновленный институт в виде DTO
     * @throws EntityNotFoundException если институт с указанным UUID не найден
     * @throws IllegalArgumentException если новое название уже используется другим институтом
     */
    @Transactional
    public InstituteDTO updateInstitute(UUID uuid, InstituteCreateUpdateDTO dto) {
        log.info("Updating institute: {}", uuid);

        Institute institute = instituteRepository.findById(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Institute not found with id: " + uuid));

        if (!institute.getTitle().equals(dto.getTitle()) &&
                instituteRepository.existsByTitle(dto.getTitle())) {
            throw new IllegalArgumentException("Institute with title '" + dto.getTitle() + "' already exists");
        }

        institute.setTitle(dto.getTitle());
        Institute updated = instituteRepository.save(institute);
        return programMapper.toInstituteDTO(updated);
    }

    /**
     * Удаляет институт по его UUID.
     * Удаление возможно только если институт не используется в программах.
     *
     * @param uuid UUID удаляемого института
     * @throws EntityNotFoundException если институт с указанным UUID не найден
     */
    @Transactional
    public void deleteInstitute(UUID uuid) {
        log.info("Deleting institute: {}", uuid);

        if (!instituteRepository.existsById(uuid)) {
            throw new EntityNotFoundException("Institute not found with id: " + uuid);
        }

        instituteRepository.deleteById(uuid);
    }
}