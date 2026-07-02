package org.urfu.controller;

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

@Slf4j
@RestController
@RequestMapping("/api/institutes")
@RequiredArgsConstructor
public class InstituteController {

    private final InstituteService instituteService;

    @PostMapping
    public ResponseEntity<InstituteDTO> createInstitute(@Valid @RequestBody InstituteCreateUpdateDTO dto) {
        log.info("POST /api/institutes - Creating institute");
        InstituteDTO created = instituteService.createInstitute(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<InstituteDTO>> getAllInstitutes() {
        log.info("GET /api/institutes - Getting all institutes");
        return ResponseEntity.ok(instituteService.getAllInstitutes());
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<InstituteDTO> getInstituteById(@PathVariable UUID uuid) {
        log.info("GET /api/institutes/{} - Getting institute by id", uuid);
        return ResponseEntity.ok(instituteService.getInstituteById(uuid));
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<InstituteDTO> updateInstitute(
            @PathVariable UUID uuid,
            @Valid @RequestBody InstituteCreateUpdateDTO dto) {
        log.info("PUT /api/institutes/{} - Updating institute", uuid);
        return ResponseEntity.ok(instituteService.updateInstitute(uuid, dto));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteInstitute(@PathVariable UUID uuid) {
        log.info("DELETE /api/institutes/{} - Deleting institute", uuid);
        instituteService.deleteInstitute(uuid);
        return ResponseEntity.noContent().build();
    }
}