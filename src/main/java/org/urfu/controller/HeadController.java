package org.urfu.controller;

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

@Slf4j
@RestController
@RequestMapping("/api/heads")
@RequiredArgsConstructor
public class HeadController {

    private final HeadService headService;

    @PostMapping
    public ResponseEntity<HeadDTO> createHead(@Valid @RequestBody HeadCreateUpdateDTO dto) {
        log.info("POST /api/heads - Creating head");
        HeadDTO created = headService.createHead(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<HeadDTO>> getAllHeads() {
        log.info("GET /api/heads - Getting all heads");
        return ResponseEntity.ok(headService.getAllHeads());
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<HeadDTO> getHeadById(@PathVariable UUID uuid) {
        log.info("GET /api/heads/{} - Getting head by id", uuid);
        return ResponseEntity.ok(headService.getHeadById(uuid));
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<HeadDTO> updateHead(
            @PathVariable UUID uuid,
            @Valid @RequestBody HeadCreateUpdateDTO dto) {
        log.info("PUT /api/heads/{} - Updating head", uuid);
        return ResponseEntity.ok(headService.updateHead(uuid, dto));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteHead(@PathVariable UUID uuid) {
        log.info("DELETE /api/heads/{} - Deleting head", uuid);
        headService.deleteHead(uuid);
        return ResponseEntity.noContent().build();
    }
}