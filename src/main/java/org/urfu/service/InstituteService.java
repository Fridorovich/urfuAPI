package org.urfu.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.urfu.dto.InstituteCreateUpdateDTO;
import org.urfu.dto.InstituteDTO;
import org.urfu.entity.Institute;
import org.urfu.mapper.ProgramMapper;
import org.urfu.repository.InstituteRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InstituteService {

    private final InstituteRepository instituteRepository;
    private final ProgramMapper programMapper;

    @Transactional
    public InstituteDTO createInstitute(InstituteCreateUpdateDTO dto) {
        log.info("Creating institute: {}", dto.getTitle());

        if (instituteRepository.existsByTitle(dto.getTitle())) {
            throw new IllegalArgumentException("Institute with title '" + dto.getTitle() + "' already exists");
        }

        Institute institute = Institute.builder()
                .title(dto.getTitle())
                .build();

        Institute saved = instituteRepository.save(institute);
        return programMapper.toInstituteDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<InstituteDTO> getAllInstitutes() {
        log.info("Getting all institutes");
        return instituteRepository.findAll().stream()
                .map(programMapper::toInstituteDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public InstituteDTO getInstituteById(UUID uuid) {
        log.info("Getting institute by id: {}", uuid);
        Institute institute = instituteRepository.findById(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Institute not found with id: " + uuid));
        return programMapper.toInstituteDTO(institute);
    }

    @Transactional
    public InstituteDTO updateInstitute(UUID uuid, InstituteCreateUpdateDTO dto) {
        log.info("Updating institute: {}", uuid);

        Institute institute = instituteRepository.findById(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Institute not found with id: " + uuid));

        if (!institute.getTitle().equals(dto.getTitle()) &&
                instituteRepository.existsByTitle(dto.getTitle())) {
            throw new IllegalArgumentException("Institute with title '" + dto.getTitle() + "' already exists");
        }

        institute.setTitle(dto.getTitle());
        Institute updated = instituteRepository.save(institute);
        return programMapper.toInstituteDTO(updated);
    }

    @Transactional
    public void deleteInstitute(UUID uuid) {
        log.info("Deleting institute: {}", uuid);

        if (!instituteRepository.existsById(uuid)) {
            throw new EntityNotFoundException("Institute not found with id: " + uuid);
        }

        instituteRepository.deleteById(uuid);
    }
}