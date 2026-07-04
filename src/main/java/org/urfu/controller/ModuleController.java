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
import org.urfu.dto.ModuleCreateUpdateDTO;
import org.urfu.dto.ModuleDTO;
import org.urfu.service.ModuleService;

import java.util.List;
import java.util.UUID;

/**
 * Контроллер для управления модулями.
 * Предоставляет полный CRUD функционал для работы с модулями образовательных программ.
 */
@Slf4j
@RestController
@RequestMapping("/api/modules")
@RequiredArgsConstructor
@Tag(name = "Module Controller", description = "API для управления модулями образовательных программ")
public class ModuleController {

    private final ModuleService moduleService;

    /**
     * Создание нового модуля.
     *
     * @param dto данные для создания модуля
     * @return созданный модуль
     */
    @Operation(
            summary = "Создание модуля",
            description = "Создает новый модуль в системе. Название должно быть уникальным. Тип модуля должен соответствовать одному из: STANDARD, PROJECT_EDUCATION, MINOR, SECTION_FK, FOREIGN_LANGUAGE."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Модуль успешно создан"),
            @ApiResponse(responseCode = "400", description = "Модуль с таким названием уже существует или некорректный тип"),
            @ApiResponse(responseCode = "401", description = "Необходима авторизация")
    })
    @PostMapping
    public ResponseEntity<ModuleDTO> createModule(
            @Parameter(description = "Данные для создания модуля", required = true)
            @Valid @RequestBody ModuleCreateUpdateDTO dto) {
        log.info("POST /api/modules - Creating module");
        ModuleDTO created = moduleService.createModule(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Получение списка всех модулей.
     *
     * @return список всех модулей
     */
    @Operation(
            summary = "Получение всех модулей",
            description = "Возвращает список всех модулей, зарегистрированных в системе."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список модулей успешно получен"),
            @ApiResponse(responseCode = "401", description = "Необходима авторизация")
    })
    @GetMapping
    public ResponseEntity<List<ModuleDTO>> getAllModules() {
        log.info("GET /api/modules - Getting all modules");
        return ResponseEntity.ok(moduleService.getAllModules());
    }

    /**
     * Получение модуля по UUID.
     *
     * @param uuid UUID модуля
     * @return модуль с указанным UUID
     */
    @Operation(
            summary = "Получение модуля по ID",
            description = "Возвращает информацию о модуле по его уникальному идентификатору."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Модуль успешно найден"),
            @ApiResponse(responseCode = "404", description = "Модуль с указанным ID не найден"),
            @ApiResponse(responseCode = "401", description = "Необходима авторизация")
    })
    @GetMapping("/{uuid}")
    public ResponseEntity<ModuleDTO> getModuleById(
            @Parameter(description = "UUID модуля", required = true, example = "54e561d2-0a27-4644-b67a-47ab4e7881bc")
            @PathVariable UUID uuid) {
        log.info("GET /api/modules/{} - Getting module by id", uuid);
        return ResponseEntity.ok(moduleService.getModuleById(uuid));
    }

    /**
     * Обновление данных модуля.
     *
     * @param uuid UUID обновляемого модуля
     * @param dto  новые данные для модуля
     * @return обновленный модуль
     */
    @Operation(
            summary = "Обновление модуля",
            description = "Обновляет данные существующего модуля. Название должно оставаться уникальным, тип должен быть корректным."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Модуль успешно обновлен"),
            @ApiResponse(responseCode = "400", description = "Модуль с таким названием уже существует или некорректный тип"),
            @ApiResponse(responseCode = "404", description = "Модуль с указанным ID не найден"),
            @ApiResponse(responseCode = "401", description = "Необходима авторизация")
    })
    @PutMapping("/{uuid}")
    public ResponseEntity<ModuleDTO> updateModule(
            @Parameter(description = "UUID модуля", required = true, example = "54e561d2-0a27-4644-b67a-47ab4e7881bc")
            @PathVariable UUID uuid,
            @Parameter(description = "Новые данные для модуля", required = true)
            @Valid @RequestBody ModuleCreateUpdateDTO dto) {
        log.info("PUT /api/modules/{} - Updating module", uuid);
        return ResponseEntity.ok(moduleService.updateModule(uuid, dto));
    }

    /**
     * Удаление модуля по UUID.
     *
     * @param uuid UUID удаляемого модуля
     * @return HTTP статус 204
     */
    @Operation(
            summary = "Удаление модуля",
            description = "Удаляет модуль из системы."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Модуль успешно удален"),
            @ApiResponse(responseCode = "404", description = "Модуль с указанным ID не найден"),
            @ApiResponse(responseCode = "400", description = "Модуль используется в программах и не может быть удален"),
            @ApiResponse(responseCode = "401", description = "Необходима авторизация")
    })
    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteModule(
            @Parameter(description = "UUID модуля", required = true, example = "54e561d2-0a27-4644-b67a-47ab4e7881bc")
            @PathVariable UUID uuid) {
        log.info("DELETE /api/modules/{} - Deleting module", uuid);
        moduleService.deleteModule(uuid);
        return ResponseEntity.noContent().build();
    }
}