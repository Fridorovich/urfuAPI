package org.urfu.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.urfu.dto.HeadCreateUpdateDTO;
import org.urfu.dto.HeadDTO;
import org.urfu.entity.Head;
import org.urfu.mapper.ProgramMapper;
import org.urfu.repository.HeadRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HeadService {

    private final HeadRepository headRepository;
    private final ProgramMapper programMapper;

    @Transactional
    public HeadDTO createHead(HeadCreateUpdateDTO dto) {
        log.info("Creating head: {}", dto.getFullname());

        if (headRepository.existsByFullname(dto.getFullname())) {
            throw new IllegalArgumentException("Head with fullname '" + dto.getFullname() + "' already exists");
        }

        Head head = Head.builder()
                .fullname(dto.getFullname())
                .build();

        Head saved = headRepository.save(head);
        return programMapper.toHeadDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<HeadDTO> getAllHeads() {
        log.info("Getting all heads");
        return headRepository.findAll().stream()
                .map(programMapper::toHeadDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public HeadDTO getHeadById(UUID uuid) {
        log.info("Getting head by id: {}", uuid);
        Head head = headRepository.findById(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Head not found with id: " + uuid));
        return programMapper.toHeadDTO(head);
    }

    @Transactional
    public HeadDTO updateHead(UUID uuid, HeadCreateUpdateDTO dto) {
        log.info("Updating head: {}", uuid);

        Head head = headRepository.findById(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Head not found with id: " + uuid));

        if (!head.getFullname().equals(dto.getFullname()) &&
                headRepository.existsByFullname(dto.getFullname())) {
            throw new IllegalArgumentException("Head with fullname '" + dto.getFullname() + "' already exists");
        }

        head.setFullname(dto.getFullname());
        Head updated = headRepository.save(head);
        return programMapper.toHeadDTO(updated);
    }

    @Transactional
    public void deleteHead(UUID uuid) {
        log.info("Deleting head: {}", uuid);

        if (!headRepository.existsById(uuid)) {
            throw new EntityNotFoundException("Head not found with id: " + uuid);
        }

        headRepository.deleteById(uuid);
    }
}