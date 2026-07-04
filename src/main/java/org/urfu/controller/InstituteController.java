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
import org.urfu.dto.InstituteCreateUpdateDTO;
import org.urfu.dto.InstituteDTO;
import org.urfu.service.InstituteService;

import java.util.List;
import java.util.UUID;

/**
 * Контроллер для управления институтами.
 * Предоставляет полный CRUD функционал для работы с институтами УрФУ.
 */
@Slf4j
@RestController
@RequestMapping("/api/institutes")
@RequiredArgsConstructor
@Tag(name = "Institute Controller", description = "API для управления институтами")
public class InstituteController {

    private final InstituteService instituteService;

    /**
     * Создание нового института.
     *
     * @param dto данные для создания института
     * @return созданный институт
     */
    @Operation(
            summary = "Создание института",
            description = "Создает новый институт в системе. Название должно быть уникальным."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Институт успешно создан"),
            @ApiResponse(responseCode = "400", description = "Институт с таким названием уже существует или некорректные данные"),
            @ApiResponse(responseCode = "401", description = "Необходима авторизация")
    })
    @PostMapping
    public ResponseEntity<InstituteDTO> createInstitute(
            @Parameter(description = "Данные для создания института", required = true)
            @Valid @RequestBody InstituteCreateUpdateDTO dto) {
        log.info("POST /api/institutes - Creating institute");
        InstituteDTO created = instituteService.createInstitute(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Получение списка всех институтов.
     *
     * @return список всех институтов
     */
    @Operation(
            summary = "Получение всех институтов",
            description = "Возвращает список всех институтов, зарегистрированных в системе."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список институтов успешно получен"),
            @ApiResponse(responseCode = "401", description = "Необходима авторизация")
    })
    @GetMapping
    public ResponseEntity<List<InstituteDTO>> getAllInstitutes() {
        log.info("GET /api/institutes - Getting all institutes");
        return ResponseEntity.ok(instituteService.getAllInstitutes());
    }

    /**
     * Получение института по UUID.
     *
     * @param uuid UUID института
     * @return институт с указанным UUID
     */
    @Operation(
            summary = "Получение института по ID",
            description = "Возвращает информацию об институте по его уникальному идентификатору."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Институт успешно найден"),
            @ApiResponse(responseCode = "404", description = "Институт с указанным ID не найден"),
            @ApiResponse(responseCode = "401", description = "Необходима авторизация")
    })
    @GetMapping("/{uuid}")
    public ResponseEntity<InstituteDTO> getInstituteById(
            @Parameter(description = "UUID института", required = true, example = "6a0099be-2bd4-409b-aee2-10984c4df380")
            @PathVariable UUID uuid) {
        log.info("GET /api/institutes/{} - Getting institute by id", uuid);
        return ResponseEntity.ok(instituteService.getInstituteById(uuid));
    }

    /**
     * Обновление данных института.
     *
     * @param uuid UUID обновляемого института
     * @param dto  новые данные для института
     * @return обновленный институт
     */
    @Operation(
            summary = "Обновление института",
            description = "Обновляет данные существующего института. Название должно оставаться уникальным."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Институт успешно обновлен"),
            @ApiResponse(responseCode = "400", description = "Институт с таким названием уже существует или некорректные данные"),
            @ApiResponse(responseCode = "404", description = "Институт с указанным ID не найден"),
            @ApiResponse(responseCode = "401", description = "Необходима авторизация")
    })
    @PutMapping("/{uuid}")
    public ResponseEntity<InstituteDTO> updateInstitute(
            @Parameter(description = "UUID института", required = true, example = "6a0099be-2bd4-409b-aee2-10984c4df380")
            @PathVariable UUID uuid,
            @Parameter(description = "Новые данные для института", required = true)
            @Valid @RequestBody InstituteCreateUpdateDTO dto) {
        log.info("PUT /api/institutes/{} - Updating institute", uuid);
        return ResponseEntity.ok(instituteService.updateInstitute(uuid, dto));
    }

    /**
     * Удаление института по UUID.
     *
     * @param uuid UUID удаляемого института
     * @return HTTP статус 204 (No Content)
     */
    @Operation(
            summary = "Удаление института",
            description = "Удаляет институт из системы."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Институт успешно удален"),
            @ApiResponse(responseCode = "404", description = "Институт с указанным ID не найден"),
            @ApiResponse(responseCode = "400", description = "Институт используется в программах и не может быть удален"),
            @ApiResponse(responseCode = "401", description = "Необходима авторизация")
    })
    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteInstitute(
            @Parameter(description = "UUID института", required = true, example = "6a0099be-2bd4-409b-aee2-10984c4df380")
            @PathVariable UUID uuid) {
        log.info("DELETE /api/institutes/{} - Deleting institute", uuid);
        instituteService.deleteInstitute(uuid);
        return ResponseEntity.noContent().build();
    }
}