package org.urfu.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.urfu.dto.ProgramCreateUpdateDTO;
import org.urfu.dto.ProgramDTO;
import org.urfu.service.ProgramService;

import java.util.List;
import java.util.UUID;

/**
 * Контроллер для управления образовательными программами.
 * Предоставляет полный CRUD функционал для работы с программами,
 * а также дополнительные эндпоинты для получения программ с модулями и сортировки.
 */
@Slf4j
@RestController
@RequestMapping("/api/programs")
@RequiredArgsConstructor
@Tag(name = "Program Controller", description = "API для управления образовательными программами")
public class ProgramController {

    private final ProgramService programService;

    /**
     * Создание новой образовательной программы.
     * При создании можно привязать существующие модули к программе.
     *
     * @param dto данные для создания программы
     * @return созданная программа с вложенными модулями
     */
    @Operation(
            summary = "Создание программы",
            description = "Создает новую образовательную программу. Требует указания института, ответственного лица и опционально модулей."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Программа успешно создана"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "404", description = "Институт или ответственное лицо не найдены"),
            @ApiResponse(responseCode = "401", description = "Необходима авторизация")
    })
    @PostMapping
    public ResponseEntity<ProgramDTO> createProgram(
            @Parameter(description = "Данные для создания программы", required = true)
            @Valid @RequestBody ProgramCreateUpdateDTO dto) {
        log.info("POST /api/programs - Creating program");
        ProgramDTO created = programService.createProgram(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Получение списка всех образовательных программ.
     * Возвращает все программы с вложенными модулями, институтами и ответственными лицами.
     *
     * @return список всех программ
     */
    @Operation(
            summary = "Получение всех программ",
            description = "Возвращает список всех образовательных программ с полной информацией о модулях, институтах и ответственных лицах."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список программ успешно получен"),
            @ApiResponse(responseCode = "401", description = "Необходима авторизация")
    })
    @GetMapping
    public ResponseEntity<List<ProgramDTO>> getAllPrograms() {
        log.info("GET /api/programs - Getting all programs");
        List<ProgramDTO> programs = programService.getAllPrograms();
        return ResponseEntity.ok(programs);
    }

    /**
     * Получение программы по UUID.
     * Возвращает полную информацию о программе с вложенными сущностями.
     *
     * @param uuid уникальный идентификатор программы
     * @return программа с указанным UUID
     */
    @Operation(
            summary = "Получение программы по ID",
            description = "Возвращает детальную информацию о программе по её UUID с вложенными модулями, институтом и ответственным лицом."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Программа успешно найдена"),
            @ApiResponse(responseCode = "404", description = "Программа с указанным ID не найдена"),
            @ApiResponse(responseCode = "401", description = "Необходима авторизация")
    })
    @GetMapping("/{uuid}")
    public ResponseEntity<ProgramDTO> getProgramById(
            @Parameter(description = "UUID программы", required = true, example = "30a12709-e029-400d-85a4-582109fbb923")
            @PathVariable UUID uuid) {
        log.info("GET /api/programs/{} - Getting program by id", uuid);
        ProgramDTO program = programService.getProgramById(uuid);
        return ResponseEntity.ok(program);
    }

    /**
     * Обновление существующей программы.
     * Позволяет изменить все поля программы и список привязанных модулей.
     *
     * @param uuid UUID обновляемой программы
     * @param dto  новые данные для программы
     * @return обновленная программа
     */
    @Operation(
            summary = "Обновление программы",
            description = "Обновляет данные существующей программы. Можно изменить название, шифр, уровень, стандарт, институт, ответственное лицо, дату аккредитации и список модулей."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Программа успешно обновлена"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "404", description = "Программа, институт или ответственное лицо не найдены"),
            @ApiResponse(responseCode = "401", description = "Необходима авторизация")
    })
    @PutMapping("/{uuid}")
    public ResponseEntity<ProgramDTO> updateProgram(
            @Parameter(description = "UUID программы", required = true, example = "30a12709-e029-400d-85a4-582109fbb923")
            @PathVariable UUID uuid,
            @Parameter(description = "Новые данные для программы", required = true)
            @Valid @RequestBody ProgramCreateUpdateDTO dto) {
        log.info("PUT /api/programs/{} - Updating program", uuid);
        ProgramDTO updated = programService.updateProgram(uuid, dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * Удаление программы по UUID.
     *
     * @param uuid UUID удаляемой программы
     * @return HTTP статус 204 (No Content)
     */
    @Operation(
            summary = "Удаление программы",
            description = "Удаляет программу по её UUID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Программа успешно удалена"),
            @ApiResponse(responseCode = "404", description = "Программа с указанным ID не найдена"),
            @ApiResponse(responseCode = "401", description = "Необходима авторизация")
    })
    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteProgram(
            @Parameter(description = "UUID программы", required = true, example = "30a12709-e029-400d-85a4-582109fbb923")
            @PathVariable UUID uuid) {
        log.info("DELETE /api/programs/{} - Deleting program", uuid);
        programService.deleteProgram(uuid);
        return ResponseEntity.noContent().build();
    }

    /**
     * Получение всех программ с вложенными модулями.
     * Аналогичен GET /api/programs, но явно указывает на включение модулей.
     *
     * @return список программ с модулями
     */
    @Operation(
            summary = "Получение программ с модулями",
            description = "Возвращает список всех образовательных программ с полной информацией о вложенных модулях."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список программ успешно получен"),
            @ApiResponse(responseCode = "401", description = "Необходима авторизация")
    })
    @GetMapping("/with-modules")
    public ResponseEntity<List<ProgramDTO>> getProgramsWithModules() {
        log.info("GET /api/programs/with-modules - Getting programs with modules");
        List<ProgramDTO> programs = programService.getProgramsWithModules();
        return ResponseEntity.ok(programs);
    }

    /**
     * Получение отсортированного списка программ.
     * Реализована ручная сортировка по полям: title, cypher, accreditationDate, standard.
     *
     * @param sortBy поле для сортировки
     * @return отсортированный список программ
     */
    @Operation(
            summary = "Сортировка программ",
            description = "Возвращает список программ, отсортированный по указанному полю. Поддерживаются поля: title, cypher, accreditationDate, standard."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список программ успешно отсортирован"),
            @ApiResponse(responseCode = "400", description = "Некорректное поле сортировки"),
            @ApiResponse(responseCode = "401", description = "Необходима авторизация")
    })
    @GetMapping("/sorted")
    public ResponseEntity<List<ProgramDTO>> getSortedPrograms(
            @Parameter(description = "Поле для сортировки", example = "title")
            @RequestParam(value = "sortBy", required = false, defaultValue = "title") String sortBy) {
        log.info("GET /api/programs/sorted - Getting programs sorted by: {}", sortBy);
        List<ProgramDTO> programs = programService.getSortedPrograms(sortBy);
        return ResponseEntity.ok(programs);
    }

    /**
     * Добавление модуля к программе.
     * Привязывает существующий модуль к указанной программе.
     *
     * @param programUuid UUID программы
     * @param moduleUuid  UUID добавляемого модуля
     * @return обновленная программа с новым модулем
     */
    @Operation(
            summary = "Добавление модуля к программе",
            description = "Привязывает существующий модуль к образовательной программе. Модуль не дублируется, если уже добавлен."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Модуль успешно добавлен к программе"),
            @ApiResponse(responseCode = "404", description = "Программа или модуль не найдены"),
            @ApiResponse(responseCode = "401", description = "Необходима авторизация")
    })
    @PutMapping("/{programUuid}/modules/{moduleUuid}")
    public ResponseEntity<ProgramDTO> addModuleToProgram(
            @Parameter(description = "UUID программы", required = true, example = "30a12709-e029-400d-85a4-582109fbb923")
            @PathVariable UUID programUuid,
            @Parameter(description = "UUID модуля", required = true, example = "54e561d2-0a27-4644-b67a-47ab4e7881bc")
            @PathVariable UUID moduleUuid) {
        log.info("PUT /api/programs/{}/modules/{} - Adding module to program", programUuid, moduleUuid);
        ProgramDTO updated = programService.addModuleToProgram(programUuid, moduleUuid);
        return ResponseEntity.ok(updated);
    }

    /**
     * Удаление модуля из программы.
     * Отвязывает модуль от программы без удаления самого модуля.
     *
     * @param programUuid UUID программы
     * @param moduleUuid  UUID удаляемого модуля
     * @return обновленная программа без удаленного модуля
     */
    @Operation(
            summary = "Удаление модуля из программы",
            description = "Отвязывает модуль от образовательной программы. Сам модуль не удаляется."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Модуль успешно удален из программы"),
            @ApiResponse(responseCode = "404", description = "Программа не найдена"),
            @ApiResponse(responseCode = "401", description = "Необходима авторизация")
    })
    @DeleteMapping("/{programUuid}/modules/{moduleUuid}")
    public ResponseEntity<ProgramDTO> removeModuleFromProgram(
            @Parameter(description = "UUID программы", required = true, example = "30a12709-e029-400d-85a4-582109fbb923")
            @PathVariable UUID programUuid,
            @Parameter(description = "UUID модуля", required = true, example = "54e561d2-0a27-4644-b67a-47ab4e7881bc")
            @PathVariable UUID moduleUuid) {
        log.info("DELETE /api/programs/{}/modules/{} - Removing module from program", programUuid, moduleUuid);
        ProgramDTO updated = programService.removeModuleFromProgram(programUuid, moduleUuid);
        return ResponseEntity.ok(updated);
    }
}