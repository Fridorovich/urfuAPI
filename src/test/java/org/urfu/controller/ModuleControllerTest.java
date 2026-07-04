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
import org.urfu.dto.ModuleCreateUpdateDTO;
import org.urfu.dto.ModuleDTO;
import org.urfu.service.ModuleService;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ModuleController.class)
class ModuleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ModuleService moduleService;

    private UUID testUuid;
    private ModuleDTO testModuleDTO;
    private ModuleCreateUpdateDTO createDTO;

    @BeforeEach
    void setUp() {
        testUuid = UUID.randomUUID();
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
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void createModule_ShouldReturnCreatedModule() throws Exception {
        when(moduleService.createModule(any(ModuleCreateUpdateDTO.class)))
                .thenReturn(testModuleDTO);

        mockMvc.perform(post("/api/modules")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.uuid").value(testUuid.toString()))
                .andExpect(jsonPath("$.title").value("Тестовый модуль"))
                .andExpect(jsonPath("$.type").value("STANDARD"));

        verify(moduleService).createModule(any(ModuleCreateUpdateDTO.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void createModule_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        ModuleCreateUpdateDTO invalidDTO = ModuleCreateUpdateDTO.builder()
                .title("")
                .type("")
                .build();

        mockMvc.perform(post("/api/modules")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(moduleService, never()).createModule(any());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void getAllModules_ShouldReturnList() throws Exception {
        List<ModuleDTO> modules = List.of(testModuleDTO);
        when(moduleService.getAllModules()).thenReturn(modules);

        mockMvc.perform(get("/api/modules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].uuid").value(testUuid.toString()))
                .andExpect(jsonPath("$[0].title").value("Тестовый модуль"))
                .andExpect(jsonPath("$[0].type").value("STANDARD"));

        verify(moduleService).getAllModules();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void getModuleById_WhenExists_ShouldReturnModule() throws Exception {
        when(moduleService.getModuleById(testUuid)).thenReturn(testModuleDTO);

        mockMvc.perform(get("/api/modules/{uuid}", testUuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(testUuid.toString()))
                .andExpect(jsonPath("$.title").value("Тестовый модуль"))
                .andExpect(jsonPath("$.type").value("STANDARD"));

        verify(moduleService).getModuleById(testUuid);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void getModuleById_WhenNotExists_ShouldReturnNotFound() throws Exception {
        when(moduleService.getModuleById(testUuid))
                .thenThrow(new EntityNotFoundException("Module not found with id: " + testUuid));

        mockMvc.perform(get("/api/modules/{uuid}", testUuid))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("Module not found")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void updateModule_WhenExists_ShouldReturnUpdatedModule() throws Exception {
        ModuleCreateUpdateDTO updateDTO = ModuleCreateUpdateDTO.builder()
                .title("Обновленный модуль")
                .type("MINOR")
                .build();

        ModuleDTO updatedDTO = ModuleDTO.builder()
                .uuid(testUuid)
                .title("Обновленный модуль")
                .type("MINOR")
                .build();

        when(moduleService.updateModule(eq(testUuid), any(ModuleCreateUpdateDTO.class)))
                .thenReturn(updatedDTO);

        mockMvc.perform(put("/api/modules/{uuid}", testUuid)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(testUuid.toString()))
                .andExpect(jsonPath("$.title").value("Обновленный модуль"))
                .andExpect(jsonPath("$.type").value("MINOR"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void deleteModule_WhenExists_ShouldReturnNoContent() throws Exception {
        doNothing().when(moduleService).deleteModule(testUuid);

        mockMvc.perform(delete("/api/modules/{uuid}", testUuid)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(moduleService).deleteModule(testUuid);
    }
}