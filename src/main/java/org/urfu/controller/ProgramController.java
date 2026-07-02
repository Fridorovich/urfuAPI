package org.urfu.controller;

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

@Slf4j
@RestController
@RequestMapping("/api/programs")
@RequiredArgsConstructor
public class ProgramController {

    private final ProgramService programService;

    @PostMapping
    public ResponseEntity<ProgramDTO> createProgram(@Valid @RequestBody ProgramCreateUpdateDTO dto) {
        log.info("POST /api/programs - Creating program");
        ProgramDTO created = programService.createProgram(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<ProgramDTO>> getAllPrograms() {
        log.info("GET /api/programs - Getting all programs");
        List<ProgramDTO> programs = programService.getAllPrograms();
        return ResponseEntity.ok(programs);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<ProgramDTO> getProgramById(@PathVariable UUID uuid) {
        log.info("GET /api/programs/{} - Getting program by id", uuid);
        ProgramDTO program = programService.getProgramById(uuid);
        return ResponseEntity.ok(program);
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<ProgramDTO> updateProgram(
            @PathVariable UUID uuid,
            @Valid @RequestBody ProgramCreateUpdateDTO dto) {
        log.info("PUT /api/programs/{} - Updating program", uuid);
        ProgramDTO updated = programService.updateProgram(uuid, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteProgram(@PathVariable UUID uuid) {
        log.info("DELETE /api/programs/{} - Deleting program", uuid);
        programService.deleteProgram(uuid);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/with-modules")
    public ResponseEntity<List<ProgramDTO>> getProgramsWithModules() {
        log.info("GET /api/programs/with-modules - Getting programs with modules");
        List<ProgramDTO> programs = programService.getProgramsWithModules();
        return ResponseEntity.ok(programs);
    }

    @GetMapping("/sorted")
    public ResponseEntity<List<ProgramDTO>> getSortedPrograms(
            @RequestParam(value = "sortBy", required = false, defaultValue = "title") String sortBy) {
        log.info("GET /api/programs/sorted - Getting programs sorted by: {}", sortBy);
        List<ProgramDTO> programs = programService.getSortedPrograms(sortBy);
        return ResponseEntity.ok(programs);
    }

    @PutMapping("/{programUuid}/modules/{moduleUuid}")
    public ResponseEntity<ProgramDTO> addModuleToProgram(
            @PathVariable UUID programUuid,
            @PathVariable UUID moduleUuid) {
        log.info("PUT /api/programs/{}/modules/{} - Adding module to program", programUuid, moduleUuid);
        ProgramDTO updated = programService.addModuleToProgram(programUuid, moduleUuid);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{programUuid}/modules/{moduleUuid}")
    public ResponseEntity<ProgramDTO> removeModuleFromProgram(
            @PathVariable UUID programUuid,
            @PathVariable UUID moduleUuid) {
        log.info("DELETE /api/programs/{}/modules/{} - Removing module from program", programUuid, moduleUuid);
        ProgramDTO updated = programService.removeModuleFromProgram(programUuid, moduleUuid);
        return ResponseEntity.ok(updated);
    }
}