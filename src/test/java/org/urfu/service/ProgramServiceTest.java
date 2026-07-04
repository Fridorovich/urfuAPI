package org.urfu.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.urfu.dto.ProgramCreateUpdateDTO;
import org.urfu.dto.ProgramDTO;
import org.urfu.entity.*;
import org.urfu.entity.Module;
import org.urfu.enums.EducationLevel;
import org.urfu.enums.EducationStandard;
import org.urfu.enums.ModuleType;
import org.urfu.mapper.ProgramMapper;
import org.urfu.repository.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Тесты для сервиса управления образовательными программами.
 */
@ExtendWith(MockitoExtension.class)
class ProgramServiceTest {

    @Mock
    private ProgramRepository programRepository;

    @Mock
    private ModuleRepository moduleRepository;

    @Mock
    private InstituteRepository instituteRepository;

    @Mock
    private HeadRepository headRepository;

    @Mock
    private ProgramMapper programMapper;

    @InjectMocks
    private ProgramService programService;

    private UUID testUuid;
    private UUID instituteUuid;
    private UUID headUuid;
    private UUID moduleUuid;
    private Program testProgram;
    private ProgramDTO testProgramDTO;
    private ProgramCreateUpdateDTO createDTO;
    private Institute testInstitute;
    private Head testHead;
    private Module testModule;

    @BeforeEach
    void setUp() {
        testUuid = UUID.randomUUID();
        instituteUuid = UUID.randomUUID();
        headUuid = UUID.randomUUID();
        moduleUuid = UUID.randomUUID();

        testInstitute = Institute.builder()
                .uuid(instituteUuid)
                .title("Тестовый институт")
                .build();

        testHead = Head.builder()
                .uuid(headUuid)
                .fullname("Тестовый руководитель")
                .build();

        testModule = Module.builder()
                .uuid(moduleUuid)
                .title("Тестовый модуль")
                .type(ModuleType.STANDARD)
                .build();

        List<Module> modules = new ArrayList<>();
        modules.add(testModule);

        testProgram = Program.builder()
                .uuid(testUuid)
                .title("Тестовая программа")
                .cypher("09.04.03/33.03")
                .level(EducationLevel.MASTER)
                .standard(EducationStandard.SUOS)
                .institute(testInstitute)
                .head(testHead)
                .accreditationDate(LocalDate.of(2025, 3, 14))
                .modules(modules)
                .build();

        testProgramDTO = ProgramDTO.builder()
                .uuid(testUuid)
                .title("Тестовая программа")
                .cypher("09.04.03/33.03")
                .level("MASTER")
                .standard("SUOS")
                .accreditationDate(LocalDate.of(2025, 3, 14))
                .modules(List.of())
                .build();

        createDTO = ProgramCreateUpdateDTO.builder()
                .title("Новая программа")
                .cypher("09.04.03/33.04")
                .level("MASTER")
                .standard("SUOS")
                .instituteUuid(instituteUuid)
                .headUuid(headUuid)
                .accreditationDate(LocalDate.of(2026, 6, 14))
                .moduleUuids(List.of(moduleUuid))
                .build();
    }

    @Test
    void createProgram_ShouldReturnCreatedProgram() {
        when(instituteRepository.findById(instituteUuid)).thenReturn(Optional.of(testInstitute));
        when(headRepository.findById(headUuid)).thenReturn(Optional.of(testHead));
        when(moduleRepository.findAllById(any())).thenReturn(List.of(testModule));
        when(programRepository.save(any(Program.class))).thenReturn(testProgram);
        when(programMapper.toDTO(testProgram)).thenReturn(testProgramDTO);

        ProgramDTO result = programService.createProgram(createDTO);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo(testProgramDTO.getTitle());
        verify(instituteRepository).findById(instituteUuid);
        verify(headRepository).findById(headUuid);
        verify(programRepository).save(any(Program.class));
    }

    @Test
    void createProgram_WhenInstituteNotFound_ShouldThrowException() {
        when(instituteRepository.findById(instituteUuid)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> programService.createProgram(createDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Institute not found");
    }

    @Test
    void createProgram_WhenHeadNotFound_ShouldThrowException() {
        when(instituteRepository.findById(instituteUuid)).thenReturn(Optional.of(testInstitute));
        when(headRepository.findById(headUuid)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> programService.createProgram(createDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Head not found");
    }

    @Test
    void getAllPrograms_ShouldReturnList() {
        List<Program> programs = List.of(testProgram);
        when(programRepository.findAllWithModules()).thenReturn(programs);
        when(programMapper.toDTO(any(Program.class))).thenReturn(testProgramDTO);

        List<ProgramDTO> result = programService.getAllPrograms();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo(testProgramDTO.getTitle());
        verify(programRepository).findAllWithModules();
    }

    @Test
    void getProgramById_WhenExists_ShouldReturnProgram() {
        when(programRepository.findByIdWithModules(testUuid)).thenReturn(Optional.of(testProgram));
        when(programMapper.toDTO(testProgram)).thenReturn(testProgramDTO);

        ProgramDTO result = programService.getProgramById(testUuid);

        assertThat(result).isNotNull();
        assertThat(result.getUuid()).isEqualTo(testUuid);
        verify(programRepository).findByIdWithModules(testUuid);
    }

    @Test
    void getProgramById_WhenNotExists_ShouldThrowException() {
        when(programRepository.findByIdWithModules(testUuid)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> programService.getProgramById(testUuid))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Program not found");
    }

    @Test
    void updateProgram_WhenExists_ShouldReturnUpdatedProgram() {
        ProgramCreateUpdateDTO updateDTO = ProgramCreateUpdateDTO.builder()
                .title("Обновленная программа")
                .cypher("09.04.03/33.05")
                .level("BACHELOR")
                .standard("FGOS_VO")
                .instituteUuid(instituteUuid)
                .headUuid(headUuid)
                .accreditationDate(LocalDate.of(2027, 1, 1))
                .moduleUuids(List.of(moduleUuid))
                .build();

        Program updatedProgram = Program.builder()
                .uuid(testUuid)
                .title("Обновленная программа")
                .cypher("09.04.03/33.05")
                .level(EducationLevel.BACHELOR)
                .standard(EducationStandard.FGOS_VO)
                .institute(testInstitute)
                .head(testHead)
                .accreditationDate(LocalDate.of(2027, 1, 1))
                .modules(List.of(testModule))
                .build();

        ProgramDTO updatedDTO = ProgramDTO.builder()
                .uuid(testUuid)
                .title("Обновленная программа")
                .cypher("09.04.03/33.05")
                .level("BACHELOR")
                .standard("FGOS_VO")
                .accreditationDate(LocalDate.of(2027, 1, 1))
                .modules(List.of())
                .build();

        when(programRepository.findById(testUuid)).thenReturn(Optional.of(testProgram));
        when(instituteRepository.findById(instituteUuid)).thenReturn(Optional.of(testInstitute));
        when(headRepository.findById(headUuid)).thenReturn(Optional.of(testHead));
        when(moduleRepository.findAllById(any())).thenReturn(List.of(testModule));
        when(programRepository.save(any(Program.class))).thenReturn(updatedProgram);
        when(programMapper.toDTO(updatedProgram)).thenReturn(updatedDTO);

        ProgramDTO result = programService.updateProgram(testUuid, updateDTO);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Обновленная программа");
        assertThat(result.getLevel()).isEqualTo("BACHELOR");
    }

    @Test
    void deleteProgram_WhenExists_ShouldDelete() {
        when(programRepository.existsById(testUuid)).thenReturn(true);
        doNothing().when(programRepository).deleteById(testUuid);

        programService.deleteProgram(testUuid);

        verify(programRepository).existsById(testUuid);
        verify(programRepository).deleteById(testUuid);
    }

    @Test
    void deleteProgram_WhenNotExists_ShouldThrowException() {
        when(programRepository.existsById(testUuid)).thenReturn(false);

        assertThatThrownBy(() -> programService.deleteProgram(testUuid))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Program not found");
    }

    @Test
    void getSortedPrograms_ShouldReturnSortedListByTitle() {
        List<Program> programs = List.of(testProgram);
        when(programRepository.findAllWithModules()).thenReturn(programs);
        when(programMapper.toDTO(any(Program.class))).thenReturn(testProgramDTO);

        List<ProgramDTO> result = programService.getSortedPrograms("title");

        assertThat(result).hasSize(1);
        verify(programRepository).findAllWithModules();
    }

    @Test
    void getSortedPrograms_WithInvalidSortField_ShouldThrowException() {
        assertThatThrownBy(() -> programService.getSortedPrograms("invalidField"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid sort field");
    }

    @Test
    void addModuleToProgram_ShouldAddModule() {
        when(programRepository.findById(testUuid)).thenReturn(Optional.of(testProgram));
        when(moduleRepository.findById(moduleUuid)).thenReturn(Optional.of(testModule));
        when(programRepository.save(any(Program.class))).thenReturn(testProgram);
        when(programMapper.toDTO(testProgram)).thenReturn(testProgramDTO);

        ProgramDTO result = programService.addModuleToProgram(testUuid, moduleUuid);

        assertThat(result).isNotNull();
        verify(programRepository).save(any(Program.class));
    }

    @Test
    void addModuleToProgram_WhenModuleAlreadyExists_ShouldNotDuplicate() {
        testProgram.setModules(List.of(testModule));

        when(programRepository.findById(testUuid)).thenReturn(Optional.of(testProgram));
        when(moduleRepository.findById(moduleUuid)).thenReturn(Optional.of(testModule));
        when(programRepository.save(any(Program.class))).thenReturn(testProgram);
        when(programMapper.toDTO(testProgram)).thenReturn(testProgramDTO);

        ProgramDTO result = programService.addModuleToProgram(testUuid, moduleUuid);

        assertThat(result).isNotNull();
        verify(programRepository).save(any(Program.class));
    }

    @Test
    void addModuleToProgram_WhenProgramNotFound_ShouldThrowException() {
        when(programRepository.findById(testUuid)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> programService.addModuleToProgram(testUuid, moduleUuid))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Program not found");
    }

    @Test
    void addModuleToProgram_WhenModuleNotFound_ShouldThrowException() {
        when(programRepository.findById(testUuid)).thenReturn(Optional.of(testProgram));
        when(moduleRepository.findById(moduleUuid)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> programService.addModuleToProgram(testUuid, moduleUuid))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Module not found");
    }

    @Test
    void removeModuleFromProgram_ShouldRemoveModule() {
        testProgram.setModules(new ArrayList<>(List.of(testModule)));

        when(programRepository.findById(testUuid)).thenReturn(Optional.of(testProgram));
        when(programRepository.save(any(Program.class))).thenReturn(testProgram);
        when(programMapper.toDTO(testProgram)).thenReturn(testProgramDTO);

        ProgramDTO result = programService.removeModuleFromProgram(testUuid, moduleUuid);

        assertThat(result).isNotNull();
        verify(programRepository).save(any(Program.class));
    }

    @Test
    void removeModuleFromProgram_WhenProgramNotFound_ShouldThrowException() {
        when(programRepository.findById(testUuid)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> programService.removeModuleFromProgram(testUuid, moduleUuid))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Program not found");
    }
}