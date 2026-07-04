package org.urfu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.urfu.dto.ProgramCreateUpdateDTO;
import org.urfu.dto.ProgramDTO;
import org.urfu.service.ProgramService;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProgramController.class)
class ProgramControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProgramService programService;

    private UUID testUuid;
    private UUID instituteUuid;
    private UUID headUuid;
    private UUID moduleUuid;
    private ProgramDTO testProgramDTO;
    private ProgramCreateUpdateDTO createDTO;

    @BeforeEach
    void setUp() {
        testUuid = UUID.randomUUID();
        instituteUuid = UUID.randomUUID();
        headUuid = UUID.randomUUID();
        moduleUuid = UUID.randomUUID();

        testProgramDTO = ProgramDTO.builder()
                .uuid(testUuid)
                .title("Тестовая программа")
                .cypher("09.04.03/33.03")
                .level("MASTER")
                .standard("SUOS")
                .accreditationDate(LocalDate.of(2025, 3, 14))
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
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void createProgram_ShouldReturnCreatedProgram() throws Exception {
        when(programService.createProgram(any(ProgramCreateUpdateDTO.class)))
                .thenReturn(testProgramDTO);

        mockMvc.perform(post("/api/programs")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.uuid").value(testUuid.toString()))
                .andExpect(jsonPath("$.title").value("Тестовая программа"))
                .andExpect(jsonPath("$.cypher").value("09.04.03/33.03"));

        verify(programService).createProgram(any(ProgramCreateUpdateDTO.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void createProgram_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        ProgramCreateUpdateDTO invalidDTO = ProgramCreateUpdateDTO.builder()
                .title("")
                .cypher("")
                .level("")
                .standard("")
                .build();

        mockMvc.perform(post("/api/programs")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(programService, never()).createProgram(any());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void getAllPrograms_ShouldReturnList() throws Exception {
        List<ProgramDTO> programs = List.of(testProgramDTO);
        when(programService.getAllPrograms()).thenReturn(programs);

        mockMvc.perform(get("/api/programs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].uuid").value(testUuid.toString()))
                .andExpect(jsonPath("$[0].title").value("Тестовая программа"));

        verify(programService).getAllPrograms();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void getProgramById_WhenExists_ShouldReturnProgram() throws Exception {
        when(programService.getProgramById(testUuid)).thenReturn(testProgramDTO);

        mockMvc.perform(get("/api/programs/{uuid}", testUuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(testUuid.toString()))
                .andExpect(jsonPath("$.title").value("Тестовая программа"));

        verify(programService).getProgramById(testUuid);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void getProgramById_WhenNotExists_ShouldReturnNotFound() throws Exception {
        when(programService.getProgramById(testUuid))
                .thenThrow(new EntityNotFoundException("Program not found with id: " + testUuid));

        mockMvc.perform(get("/api/programs/{uuid}", testUuid))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("Program not found")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void updateProgram_WhenExists_ShouldReturnUpdatedProgram() throws Exception {
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

        ProgramDTO updatedDTO = ProgramDTO.builder()
                .uuid(testUuid)
                .title("Обновленная программа")
                .cypher("09.04.03/33.05")
                .level("BACHELOR")
                .standard("FGOS_VO")
                .accreditationDate(LocalDate.of(2027, 1, 1))
                .build();

        when(programService.updateProgram(eq(testUuid), any(ProgramCreateUpdateDTO.class)))
                .thenReturn(updatedDTO);

        mockMvc.perform(put("/api/programs/{uuid}", testUuid)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(testUuid.toString()))
                .andExpect(jsonPath("$.title").value("Обновленная программа"))
                .andExpect(jsonPath("$.cypher").value("09.04.03/33.05"))
                .andExpect(jsonPath("$.level").value("BACHELOR"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void deleteProgram_WhenExists_ShouldReturnNoContent() throws Exception {
        doNothing().when(programService).deleteProgram(testUuid);

        mockMvc.perform(delete("/api/programs/{uuid}", testUuid)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(programService).deleteProgram(testUuid);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void getProgramsWithModules_ShouldReturnList() throws Exception {
        List<ProgramDTO> programs = List.of(testProgramDTO);
        when(programService.getProgramsWithModules()).thenReturn(programs);

        mockMvc.perform(get("/api/programs/with-modules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(programService).getProgramsWithModules();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void getSortedPrograms_ShouldReturnSortedList() throws Exception {
        List<ProgramDTO> programs = List.of(testProgramDTO);
        when(programService.getSortedPrograms("title")).thenReturn(programs);

        mockMvc.perform(get("/api/programs/sorted")
                        .param("sortBy", "title"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(programService).getSortedPrograms("title");
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void getSortedPrograms_WithInvalidSortField_ShouldReturnBadRequest() throws Exception {
        when(programService.getSortedPrograms("invalid"))
                .thenThrow(new IllegalArgumentException("Invalid sort field: invalid"));

        mockMvc.perform(get("/api/programs/sorted")
                        .param("sortBy", "invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Invalid sort field")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void addModuleToProgram_ShouldReturnUpdatedProgram() throws Exception {
        when(programService.addModuleToProgram(testUuid, moduleUuid))
                .thenReturn(testProgramDTO);

        mockMvc.perform(put("/api/programs/{programUuid}/modules/{moduleUuid}", testUuid, moduleUuid)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(testUuid.toString()));

        verify(programService).addModuleToProgram(testUuid, moduleUuid);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void removeModuleFromProgram_ShouldReturnUpdatedProgram() throws Exception {
        when(programService.removeModuleFromProgram(testUuid, moduleUuid))
                .thenReturn(testProgramDTO);

        mockMvc.perform(delete("/api/programs/{programUuid}/modules/{moduleUuid}", testUuid, moduleUuid)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(testUuid.toString()));

        verify(programService).removeModuleFromProgram(testUuid, moduleUuid);
    }

    @Test
    void getPrograms_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/programs"))
                .andExpect(status().isUnauthorized());

        verify(programService, never()).getAllPrograms();
    }
}