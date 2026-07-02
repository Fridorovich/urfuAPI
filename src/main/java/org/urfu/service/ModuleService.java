package org.urfu.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.urfu.dto.ModuleCreateUpdateDTO;
import org.urfu.dto.ModuleDTO;
import org.urfu.entity.Module;
import org.urfu.enums.ModuleType;
import org.urfu.mapper.ProgramMapper;
import org.urfu.repository.ModuleRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModuleService {

    private final ModuleRepository moduleRepository;
    private final ProgramMapper programMapper;

    @Transactional
    public ModuleDTO createModule(ModuleCreateUpdateDTO dto) {
        log.info("Creating module: {}", dto.getTitle());

        if (moduleRepository.existsByTitle(dto.getTitle())) {
            throw new IllegalArgumentException("Module with title '" + dto.getTitle() + "' already exists");
        }

        Module module = Module.builder()
                .title(dto.getTitle())
                .type(ModuleType.valueOf(dto.getType()))
                .build();

        Module saved = moduleRepository.save(module);
        return programMapper.toModuleDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<ModuleDTO> getAllModules() {
        log.info("Getting all modules");
        return moduleRepository.findAll().stream()
                .map(programMapper::toModuleDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ModuleDTO getModuleById(UUID uuid) {
        log.info("Getting module by id: {}", uuid);
        Module module = moduleRepository.findById(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Module not found with id: " + uuid));
        return programMapper.toModuleDTO(module);
    }

    @Transactional
    public ModuleDTO updateModule(UUID uuid, ModuleCreateUpdateDTO dto) {
        log.info("Updating module: {}", uuid);

        Module module = moduleRepository.findById(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Module not found with id: " + uuid));

        if (!module.getTitle().equals(dto.getTitle()) &&
                moduleRepository.existsByTitle(dto.getTitle())) {
            throw new IllegalArgumentException("Module with title '" + dto.getTitle() + "' already exists");
        }

        module.setTitle(dto.getTitle());
        module.setType(ModuleType.valueOf(dto.getType()));

        Module updated = moduleRepository.save(module);
        return programMapper.toModuleDTO(updated);
    }

    @Transactional
    public void deleteModule(UUID uuid) {
        log.info("Deleting module: {}", uuid);

        if (!moduleRepository.existsById(uuid)) {
            throw new EntityNotFoundException("Module not found with id: " + uuid);
        }

        moduleRepository.deleteById(uuid);
    }
}