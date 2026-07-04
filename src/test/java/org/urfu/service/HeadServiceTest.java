package org.urfu.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.urfu.dto.HeadCreateUpdateDTO;
import org.urfu.dto.HeadDTO;
import org.urfu.entity.Head;
import org.urfu.mapper.ProgramMapper;
import org.urfu.repository.HeadRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Тесты для сервиса управления ответственными лицами.
 */
@ExtendWith(MockitoExtension.class)
class HeadServiceTest {

    @Mock
    private HeadRepository headRepository;

    @Mock
    private ProgramMapper programMapper;

    @InjectMocks
    private HeadService headService;

    private UUID testUuid;
    private Head testHead;
    private HeadDTO testHeadDTO;
    private HeadCreateUpdateDTO createDTO;

    @BeforeEach
    void setUp() {
        testUuid = UUID.randomUUID();
        testHead = Head.builder()
                .uuid(testUuid)
                .fullname("Иванов Иван Иванович")
                .build();

        testHeadDTO = HeadDTO.builder()
                .uuid(testUuid)
                .fullname("Иванов Иван Иванович")
                .build();

        createDTO = HeadCreateUpdateDTO.builder()
                .fullname("Петров Петр Петрович")
                .build();
    }

    @Test
    void createHead_ShouldReturnCreatedHead() {
        when(headRepository.existsByFullname(createDTO.getFullname())).thenReturn(false);
        when(headRepository.save(any(Head.class))).thenReturn(testHead);
        when(programMapper.toHeadDTO(testHead)).thenReturn(testHeadDTO);

        HeadDTO result = headService.createHead(createDTO);

        assertThat(result).isNotNull();
        assertThat(result.getFullname()).isEqualTo(testHeadDTO.getFullname());
        verify(headRepository).existsByFullname(createDTO.getFullname());
        verify(headRepository).save(any(Head.class));
    }

    @Test
    void createHead_WhenFullnameExists_ShouldThrowException() {
        when(headRepository.existsByFullname(createDTO.getFullname())).thenReturn(true);

        assertThatThrownBy(() -> headService.createHead(createDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");

        verify(headRepository, never()).save(any(Head.class));
    }

    @Test
    void getAllHeads_ShouldReturnList() {
        List<Head> heads = List.of(testHead);
        when(headRepository.findAll()).thenReturn(heads);
        when(programMapper.toHeadDTO(any(Head.class))).thenReturn(testHeadDTO);

        List<HeadDTO> result = headService.getAllHeads();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFullname()).isEqualTo(testHeadDTO.getFullname());
        verify(headRepository).findAll();
    }

    @Test
    void getHeadById_WhenExists_ShouldReturnHead() {
        when(headRepository.findById(testUuid)).thenReturn(Optional.of(testHead));
        when(programMapper.toHeadDTO(testHead)).thenReturn(testHeadDTO);

        HeadDTO result = headService.getHeadById(testUuid);

        assertThat(result).isNotNull();
        assertThat(result.getUuid()).isEqualTo(testUuid);
        verify(headRepository).findById(testUuid);
    }

    @Test
    void getHeadById_WhenNotExists_ShouldThrowException() {
        when(headRepository.findById(testUuid)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> headService.getHeadById(testUuid))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Head not found");
    }

    @Test
    void updateHead_WhenExists_ShouldReturnUpdatedHead() {
        HeadCreateUpdateDTO updateDTO = HeadCreateUpdateDTO.builder()
                .fullname("Сидоров Сидор Сидорович")
                .build();

        Head updatedHead = Head.builder()
                .uuid(testUuid)
                .fullname("Сидоров Сидор Сидорович")
                .build();

        HeadDTO updatedDTO = HeadDTO.builder()
                .uuid(testUuid)
                .fullname("Сидоров Сидор Сидорович")
                .build();

        when(headRepository.findById(testUuid)).thenReturn(Optional.of(testHead));
        when(headRepository.existsByFullname(updateDTO.getFullname())).thenReturn(false);
        when(headRepository.save(any(Head.class))).thenReturn(updatedHead);
        when(programMapper.toHeadDTO(updatedHead)).thenReturn(updatedDTO);

        HeadDTO result = headService.updateHead(testUuid, updateDTO);

        assertThat(result).isNotNull();
        assertThat(result.getFullname()).isEqualTo("Сидоров Сидор Сидорович");
    }

    @Test
    void deleteHead_WhenExists_ShouldDelete() {
        when(headRepository.existsById(testUuid)).thenReturn(true);
        doNothing().when(headRepository).deleteById(testUuid);

        headService.deleteHead(testUuid);

        verify(headRepository).existsById(testUuid);
        verify(headRepository).deleteById(testUuid);
    }

    @Test
    void deleteHead_WhenNotExists_ShouldThrowException() {
        when(headRepository.existsById(testUuid)).thenReturn(false);

        assertThatThrownBy(() -> headService.deleteHead(testUuid))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Head not found");
    }
}