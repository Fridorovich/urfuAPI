package org.urfu.controller;

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

@Slf4j
@RestController
@RequestMapping("/api/modules")
@RequiredArgsConstructor
public class ModuleController {

    private final ModuleService moduleService;

    @PostMapping
    public ResponseEntity<ModuleDTO> createModule(@Valid @RequestBody ModuleCreateUpdateDTO dto) {
        log.info("POST /api/modules - Creating module");
        ModuleDTO created = moduleService.createModule(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<ModuleDTO>> getAllModules() {
        log.info("GET /api/modules - Getting all modules");
        return ResponseEntity.ok(moduleService.getAllModules());
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<ModuleDTO> getModuleById(@PathVariable UUID uuid) {
        log.info("GET /api/modules/{} - Getting module by id", uuid);
        return ResponseEntity.ok(moduleService.getModuleById(uuid));
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<ModuleDTO> updateModule(
            @PathVariable UUID uuid,
            @Valid @RequestBody ModuleCreateUpdateDTO dto) {
        log.info("PUT /api/modules/{} - Updating module", uuid);
        return ResponseEntity.ok(moduleService.updateModule(uuid, dto));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteModule(@PathVariable UUID uuid) {
        log.info("DELETE /api/modules/{} - Deleting module", uuid);
        moduleService.deleteModule(uuid);
        return ResponseEntity.noContent().build();
    }
}