package org.urfu.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.urfu.dto.ModuleCreateUpdateDTO;
import org.urfu.dto.ModuleDTO;
import org.urfu.entity.Module;
import org.urfu.enums.ModuleType;
import org.urfu.mapper.ProgramMapper;
import org.urfu.repository.ModuleRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Тесты для сервиса управления модулями.
 */
@ExtendWith(MockitoExtension.class)
class ModuleServiceTest {

    @Mock
    private ModuleRepository moduleRepository;

    @Mock
    private ProgramMapper programMapper;

    @InjectMocks
    private ModuleService moduleService;

    private UUID testUuid;
    private Module testModule;
    private ModuleDTO testModuleDTO;
    private ModuleCreateUpdateDTO createDTO;

    @BeforeEach
    void setUp() {
        testUuid = UUID.randomUUID();
        testModule = Module.builder()
                .uuid(testUuid)
                .title("Тестовый модуль")
                .type(ModuleType.STANDARD)
                .build();

        testModuleDTO = ModuleDTO.builder()
                .uuid(testUuid)
                .title("Тестовый модуль")
                .type("STANDARD")
                .build();

        createDTO = ModuleCreateUpdateDTO.builder()
                .title("Новый модуль")
                .type("PROJECT_EDUCATION")
                .build();
    }

    @Test
    void createModule_ShouldReturnCreatedModule() {
        when(moduleRepository.existsByTitle(createDTO.getTitle())).thenReturn(false);
        when(moduleRepository.save(any(Module.class))).thenReturn(testModule);
        when(programMapper.toModuleDTO(testModule)).thenReturn(testModuleDTO);

        ModuleDTO result = moduleService.createModule(createDTO);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo(testModuleDTO.getTitle());
        verify(moduleRepository).existsByTitle(createDTO.getTitle());
        verify(moduleRepository).save(any(Module.class));
    }

    @Test
    void createModule_WhenTitleExists_ShouldThrowException() {
        when(moduleRepository.existsByTitle(createDTO.getTitle())).thenReturn(true);

        assertThatThrownBy(() -> moduleService.createModule(createDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void createModule_WithInvalidType_ShouldThrowException() {
        ModuleCreateUpdateDTO invalidDTO = ModuleCreateUpdateDTO.builder()
                .title("Модуль")
                .type("INVALID_TYPE")
                .build();

        assertThatThrownBy(() -> moduleService.createModule(invalidDTO))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getAllModules_ShouldReturnList() {
        List<Module> modules = List.of(testModule);
        when(moduleRepository.findAll()).thenReturn(modules);
        when(programMapper.toModuleDTO(any(Module.class))).thenReturn(testModuleDTO);

        List<ModuleDTO> result = moduleService.getAllModules();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo(testModuleDTO.getTitle());
    }

    @Test
    void getModuleById_WhenExists_ShouldReturnModule() {
        when(moduleRepository.findById(testUuid)).thenReturn(Optional.of(testModule));
        when(programMapper.toModuleDTO(testModule)).thenReturn(testModuleDTO);

        ModuleDTO result = moduleService.getModuleById(testUuid);

        assertThat(result).isNotNull();
        assertThat(result.getUuid()).isEqualTo(testUuid);
    }

    @Test
    void getModuleById_WhenNotExists_ShouldThrowException() {
        when(moduleRepository.findById(testUuid)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> moduleService.getModuleById(testUuid))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Module not found");
    }

    @Test
    void updateModule_WhenExists_ShouldReturnUpdatedModule() {
        ModuleCreateUpdateDTO updateDTO = ModuleCreateUpdateDTO.builder()
                .title("Обновленный модуль")
                .type("MINOR")
                .build();

        Module updatedModule = Module.builder()
                .uuid(testUuid)
                .title("Обновленный модуль")
                .type(ModuleType.MINOR)
                .build();

        ModuleDTO updatedDTO = ModuleDTO.builder()
                .uuid(testUuid)
                .title("Обновленный модуль")
                .type("MINOR")
                .build();

        when(moduleRepository.findById(testUuid)).thenReturn(Optional.of(testModule));
        when(moduleRepository.existsByTitle(updateDTO.getTitle())).thenReturn(false);
        when(moduleRepository.save(any(Module.class))).thenReturn(updatedModule);
        when(programMapper.toModuleDTO(updatedModule)).thenReturn(updatedDTO);

        ModuleDTO result = moduleService.updateModule(testUuid, updateDTO);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Обновленный модуль");
        assertThat(result.getType()).isEqualTo("MINOR");
    }

    @Test
    void deleteModule_WhenExists_ShouldDelete() {
        when(moduleRepository.existsById(testUuid)).thenReturn(true);
        doNothing().when(moduleRepository).deleteById(testUuid);

        moduleService.deleteModule(testUuid);

        verify(moduleRepository).existsById(testUuid);
        verify(moduleRepository).deleteById(testUuid);
    }

    @Test
    void deleteModule_WhenNotExists_ShouldThrowException() {
        when(moduleRepository.existsById(testUuid)).thenReturn(false);

        assertThatThrownBy(() -> moduleService.deleteModule(testUuid))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Module not found");
    }
}