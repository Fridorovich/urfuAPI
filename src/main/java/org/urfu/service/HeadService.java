package org.urfu.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.urfu.dto.HeadCreateUpdateDTO;
import org.urfu.dto.HeadDTO;
import org.urfu.entity.Head;
import org.urfu.mapper.ProgramMapper;
import org.urfu.repository.HeadRepository;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Сервис для управления ответственными лицами.
 * Предоставляет полный CRUD функционал для работы с ответственными лицами образовательных программ.
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HeadService {

    private final HeadRepository headRepository;
    private final ProgramMapper programMapper;

    /**
     * Создает новое ответственное лицо.
     * Перед созданием проверяет, что лицо с таким ФИО не существует.
     *
     * @param dto данные для создания ответственного лица
     * @return созданное ответственное лицо в виде DTO
     * @throws IllegalArgumentException если лицо с таким ФИО уже существует
     */
    @Transactional
    public HeadDTO createHead(HeadCreateUpdateDTO dto) {
        log.info("Creating head: {}", dto.getFullname());

        if (headRepository.existsByFullname(dto.getFullname())) {
            throw new IllegalArgumentException("Head with fullname '" + dto.getFullname() + "' already exists");
        }

        Head head = Head.builder()
                .fullname(dto.getFullname())
                .build();

        Head saved = headRepository.save(head);
        return programMapper.toHeadDTO(saved);
    }

    /**
     * Получает список всех ответственных лиц.
     *
     * @return список всех ответственных лиц в виде DTO
     */
    @Transactional(readOnly = true)
    public List<HeadDTO> getAllHeads() {
        log.info("Getting all heads");
        return headRepository.findAll().stream()
                .map(programMapper::toHeadDTO)
                .collect(Collectors.toList());
    }

    /**
     * Получает ответственное лицо по его UUID.
     *
     * @param uuid UUID ответственного лица
     * @return ответственное лицо в виде DTO
     * @throws EntityNotFoundException если лицо с указанным UUID не найдено
     */
    @Transactional(readOnly = true)
    public HeadDTO getHeadById(UUID uuid) {
        log.info("Getting head by id: {}", uuid);
        Head head = headRepository.findById(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Head not found with id: " + uuid));
        return programMapper.toHeadDTO(head);
    }

    /**
     * Обновляет данные существующего ответственного лица.
     * Если изменяется ФИО, проверяет, что новое ФИО не занято другим лицом.
     *
     * @param uuid UUID обновляемого ответственного лица
     * @param dto новые данные для ответственного лица
     * @return обновленное ответственное лицо в виде DTO
     * @throws EntityNotFoundException если лицо с указанным UUID не найдено
     * @throws IllegalArgumentException если новое ФИО уже используется другим лицом
     */
    @Transactional
    public HeadDTO updateHead(UUID uuid, HeadCreateUpdateDTO dto) {
        log.info("Updating head: {}", uuid);

        Head head = headRepository.findById(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Head not found with id: " + uuid));

        if (!head.getFullname().equals(dto.getFullname()) &&
                headRepository.existsByFullname(dto.getFullname())) {
            throw new IllegalArgumentException("Head with fullname '" + dto.getFullname() + "' already exists");
        }

        head.setFullname(dto.getFullname());
        Head updated = headRepository.save(head);
        return programMapper.toHeadDTO(updated);
    }

    /**
     * Удаляет ответственное лицо по его UUID.
     * Удаление возможно только если лицо не используется в программах.
     *
     * @param uuid UUID удаляемого ответственного лица
     * @throws EntityNotFoundException если лицо с указанным UUID не найдено
     */
    @Transactional
    public void deleteHead(UUID uuid) {
        log.info("Deleting head: {}", uuid);

        if (!headRepository.existsById(uuid)) {
            throw new EntityNotFoundException("Head not found with id: " + uuid);
        }

        headRepository.deleteById(uuid);
    }
}