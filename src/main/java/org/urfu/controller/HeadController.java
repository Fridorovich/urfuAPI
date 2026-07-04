package org.urfu.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.urfu.dto.HeadCreateUpdateDTO;
import org.urfu.dto.HeadDTO;
import org.urfu.service.HeadService;

import java.util.List;
import java.util.UUID;

/**
 * Контроллер для управления ответственными лицами.
 * Предоставляет полный CRUD функционал для работы с ответственными лицами образовательных программ.
 */
@Slf4j
@RestController
@RequestMapping("/api/heads")
@RequiredArgsConstructor
@Tag(name = "Head Controller", description = "API для управления ответственными лицами")
public class HeadController {

    private final HeadService headService;

    /**
     * Создание нового ответственного лица.
     *
     * @param dto данные для создания ответственного лица
     * @return созданное ответственное лицо
     */
    @Operation(
            summary = "Создание ответственного лица",
            description = "Создает новое ответственное лицо в системе. ФИО должно быть уникальным."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Ответственное лицо успешно создано"),
            @ApiResponse(responseCode = "400", description = "Ответственное лицо с таким ФИО уже существует или некорректные данные"),
            @ApiResponse(responseCode = "401", description = "Необходима авторизация")
    })
    @PostMapping
    public ResponseEntity<HeadDTO> createHead(
            @Parameter(description = "Данные для создания ответственного лица", required = true)
            @Valid @RequestBody HeadCreateUpdateDTO dto) {
        log.info("POST /api/heads - Creating head");
        HeadDTO created = headService.createHead(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Получение списка всех ответственных лиц.
     *
     * @return список всех ответственных лиц
     */
    @Operation(
            summary = "Получение всех ответственных лиц",
            description = "Возвращает список всех ответственных лиц, зарегистрированных в системе."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список ответственных лиц успешно получен"),
            @ApiResponse(responseCode = "401", description = "Необходима авторизация")
    })
    @GetMapping
    public ResponseEntity<List<HeadDTO>> getAllHeads() {
        log.info("GET /api/heads - Getting all heads");
        return ResponseEntity.ok(headService.getAllHeads());
    }

    /**
     * Получение ответственного лица по UUID.
     *
     * @param uuid UUID ответственного лица
     * @return ответственное лицо с указанным UUID
     */
    @Operation(
            summary = "Получение ответственного лица по ID",
            description = "Возвращает информацию об ответственном лице по его уникальному идентификатору."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ответственное лицо успешно найдено"),
            @ApiResponse(responseCode = "404", description = "Ответственное лицо с указанным ID не найдено"),
            @ApiResponse(responseCode = "401", description = "Необходима авторизация")
    })
    @GetMapping("/{uuid}")
    public ResponseEntity<HeadDTO> getHeadById(
            @Parameter(description = "UUID ответственного лица", required = true, example = "0e887f76-2a05-11e1-b174-00259030b74f")
            @PathVariable UUID uuid) {
        log.info("GET /api/heads/{} - Getting head by id", uuid);
        return ResponseEntity.ok(headService.getHeadById(uuid));
    }

    /**
     * Обновление данных ответственного лица.
     *
     * @param uuid UUID обновляемого ответственного лица
     * @param dto  новые данные для ответственного лица
     * @return обновленное ответственное лицо
     */
    @Operation(
            summary = "Обновление ответственного лица",
            description = "Обновляет данные существующего ответственного лица. ФИО должно оставаться уникальным."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ответственное лицо успешно обновлено"),
            @ApiResponse(responseCode = "400", description = "Ответственное лицо с таким ФИО уже существует или некорректные данные"),
            @ApiResponse(responseCode = "404", description = "Ответственное лицо с указанным ID не найдено"),
            @ApiResponse(responseCode = "401", description = "Необходима авторизация")
    })
    @PutMapping("/{uuid}")
    public ResponseEntity<HeadDTO> updateHead(
            @Parameter(description = "UUID ответственного лица", required = true, example = "0e887f76-2a05-11e1-b174-00259030b74f")
            @PathVariable UUID uuid,
            @Parameter(description = "Новые данные для ответственного лица", required = true)
            @Valid @RequestBody HeadCreateUpdateDTO dto) {
        log.info("PUT /api/heads/{} - Updating head", uuid);
        return ResponseEntity.ok(headService.updateHead(uuid, dto));
    }

    /**
     * Удаление ответственного лица по UUID.
     *
     * @param uuid UUID удаляемого ответственного лица
     * @return HTTP статус 204
     */
    @Operation(
            summary = "Удаление ответственного лица",
            description = "Удаляет ответственное лицо из системы."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Ответственное лицо успешно удалено"),
            @ApiResponse(responseCode = "404", description = "Ответственное лицо с указанным ID не найдено"),
            @ApiResponse(responseCode = "400", description = "Ответственное лицо используется в программах и не может быть удалено"),
            @ApiResponse(responseCode = "401", description = "Необходима авторизация")
    })
    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteHead(
            @Parameter(description = "UUID ответственного лица", required = true, example = "0e887f76-2a05-11e1-b174-00259030b74f")
            @PathVariable UUID uuid) {
        log.info("DELETE /api/heads/{} - Deleting head", uuid);
        headService.deleteHead(uuid);
        return ResponseEntity.noContent().build();
    }
}