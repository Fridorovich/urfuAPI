package org.urfu.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.urfu.dto.InstituteCreateUpdateDTO;
import org.urfu.dto.InstituteDTO;
import org.urfu.entity.Institute;
import org.urfu.mapper.ProgramMapper;
import org.urfu.repository.InstituteRepository;
import org.urfu.service.InstituteService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Тесты для сервиса управления институтами.
 * Проверяет все CRUD операции и обработку исключений.
 */
@ExtendWith(MockitoExtension.class)
class InstituteServiceTest {

    @Mock
    private InstituteRepository instituteRepository;

    @Mock
    private ProgramMapper programMapper;

    @InjectMocks
    private InstituteService instituteService;

    private UUID testUuid;
    private Institute testInstitute;
    private InstituteDTO testInstituteDTO;
    private InstituteCreateUpdateDTO createDTO;

    @BeforeEach
    void setUp() {
        testUuid = UUID.randomUUID();
        testInstitute = Institute.builder()
                .uuid(testUuid)
                .title("Тестовый институт")
                .build();

        testInstituteDTO = InstituteDTO.builder()
                .uuid(testUuid)
                .title("Тестовый институт")
                .build();

        createDTO = InstituteCreateUpdateDTO.builder()
                .title("Новый институт")
                .build();
    }

    @Test
    void createInstitute_ShouldReturnCreatedInstitute() {
        when(instituteRepository.existsByTitle(createDTO.getTitle())).thenReturn(false);
        when(instituteRepository.save(any(Institute.class))).thenReturn(testInstitute);
        when(programMapper.toInstituteDTO(testInstitute)).thenReturn(testInstituteDTO);

        InstituteDTO result = instituteService.createInstitute(createDTO);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo(testInstituteDTO.getTitle());
        verify(instituteRepository).existsByTitle(createDTO.getTitle());
        verify(instituteRepository).save(any(Institute.class));
    }

    @Test
    void createInstitute_WhenTitleExists_ShouldThrowException() {
        when(instituteRepository.existsByTitle(createDTO.getTitle())).thenReturn(true);

        assertThatThrownBy(() -> instituteService.createInstitute(createDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");

        verify(instituteRepository, never()).save(any(Institute.class));
    }

    @Test
    void getAllInstitutes_ShouldReturnList() {
        List<Institute> institutes = List.of(testInstitute);
        when(instituteRepository.findAll()).thenReturn(institutes);
        when(programMapper.toInstituteDTO(any(Institute.class))).thenReturn(testInstituteDTO);

        List<InstituteDTO> result = instituteService.getAllInstitutes();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo(testInstituteDTO.getTitle());
        verify(instituteRepository).findAll();
    }

    @Test
    void getInstituteById_WhenExists_ShouldReturnInstitute() {
        when(instituteRepository.findById(testUuid)).thenReturn(Optional.of(testInstitute));
        when(programMapper.toInstituteDTO(testInstitute)).thenReturn(testInstituteDTO);

        InstituteDTO result = instituteService.getInstituteById(testUuid);

        assertThat(result).isNotNull();
        assertThat(result.getUuid()).isEqualTo(testUuid);
        verify(instituteRepository).findById(testUuid);
    }

    @Test
    void getInstituteById_WhenNotExists_ShouldThrowException() {
        when(instituteRepository.findById(testUuid)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> instituteService.getInstituteById(testUuid))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Institute not found");

        verify(instituteRepository).findById(testUuid);
    }

    @Test
    void updateInstitute_WhenExists_ShouldReturnUpdatedInstitute() {
        InstituteCreateUpdateDTO updateDTO = InstituteCreateUpdateDTO.builder()
                .title("Обновленный институт")
                .build();

        Institute updatedInstitute = Institute.builder()
                .uuid(testUuid)
                .title("Обновленный институт")
                .build();

        InstituteDTO updatedDTO = InstituteDTO.builder()
                .uuid(testUuid)
                .title("Обновленный институт")
                .build();

        when(instituteRepository.findById(testUuid)).thenReturn(Optional.of(testInstitute));
        when(instituteRepository.existsByTitle(updateDTO.getTitle())).thenReturn(false);
        when(instituteRepository.save(any(Institute.class))).thenReturn(updatedInstitute);
        when(programMapper.toInstituteDTO(updatedInstitute)).thenReturn(updatedDTO);

        InstituteDTO result = instituteService.updateInstitute(testUuid, updateDTO);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Обновленный институт");
        verify(instituteRepository).findById(testUuid);
        verify(instituteRepository).save(any(Institute.class));
    }

    @Test
    void updateInstitute_WhenNotExists_ShouldThrowException() {
        when(instituteRepository.findById(testUuid)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> instituteService.updateInstitute(testUuid, createDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Institute not found");

        verify(instituteRepository, never()).save(any(Institute.class));
    }

    @Test
    void deleteInstitute_WhenExists_ShouldDelete() {
        when(instituteRepository.existsById(testUuid)).thenReturn(true);
        doNothing().when(instituteRepository).deleteById(testUuid);

        instituteService.deleteInstitute(testUuid);

        verify(instituteRepository).existsById(testUuid);
        verify(instituteRepository).deleteById(testUuid);
    }

    @Test
    void deleteInstitute_WhenNotExists_ShouldThrowException() {
        when(instituteRepository.existsById(testUuid)).thenReturn(false);

        assertThatThrownBy(() -> instituteService.deleteInstitute(testUuid))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Institute not found");

        verify(instituteRepository, never()).deleteById(any());
    }
}