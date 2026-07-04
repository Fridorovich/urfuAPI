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
import org.urfu.dto.InstituteCreateUpdateDTO;
import org.urfu.dto.InstituteDTO;
import org.urfu.service.InstituteService;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InstituteController.class)
class InstituteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private InstituteService instituteService;

    private UUID testUuid;
    private InstituteDTO testInstituteDTO;
    private InstituteCreateUpdateDTO createDTO;

    @BeforeEach
    void setUp() {
        testUuid = UUID.randomUUID();
        testInstituteDTO = InstituteDTO.builder()
                .uuid(testUuid)
                .title("Тестовый институт")
                .build();

        createDTO = InstituteCreateUpdateDTO.builder()
                .title("Новый институт")
                .build();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void createInstitute_ShouldReturnCreatedInstitute() throws Exception {
        when(instituteService.createInstitute(any(InstituteCreateUpdateDTO.class)))
                .thenReturn(testInstituteDTO);

        mockMvc.perform(post("/api/institutes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.uuid").value(testUuid.toString()))
                .andExpect(jsonPath("$.title").value("Тестовый институт"));

        verify(instituteService).createInstitute(any(InstituteCreateUpdateDTO.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void createInstitute_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        InstituteCreateUpdateDTO invalidDTO = InstituteCreateUpdateDTO.builder()
                .title("")
                .build();

        mockMvc.perform(post("/api/institutes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(instituteService, never()).createInstitute(any());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void getAllInstitutes_ShouldReturnList() throws Exception {
        List<InstituteDTO> institutes = List.of(testInstituteDTO);
        when(instituteService.getAllInstitutes()).thenReturn(institutes);

        mockMvc.perform(get("/api/institutes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].uuid").value(testUuid.toString()))
                .andExpect(jsonPath("$[0].title").value("Тестовый институт"));

        verify(instituteService).getAllInstitutes();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void getInstituteById_WhenExists_ShouldReturnInstitute() throws Exception {
        when(instituteService.getInstituteById(testUuid)).thenReturn(testInstituteDTO);

        mockMvc.perform(get("/api/institutes/{uuid}", testUuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(testUuid.toString()))
                .andExpect(jsonPath("$.title").value("Тестовый институт"));

        verify(instituteService).getInstituteById(testUuid);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void getInstituteById_WhenNotExists_ShouldReturnNotFound() throws Exception {
        when(instituteService.getInstituteById(testUuid))
                .thenThrow(new EntityNotFoundException("Institute not found with id: " + testUuid));

        mockMvc.perform(get("/api/institutes/{uuid}", testUuid))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("Institute not found")));

        verify(instituteService).getInstituteById(testUuid);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void updateInstitute_WhenExists_ShouldReturnUpdatedInstitute() throws Exception {
        InstituteCreateUpdateDTO updateDTO = InstituteCreateUpdateDTO.builder()
                .title("Обновленный институт")
                .build();

        InstituteDTO updatedDTO = InstituteDTO.builder()
                .uuid(testUuid)
                .title("Обновленный институт")
                .build();

        when(instituteService.updateInstitute(eq(testUuid), any(InstituteCreateUpdateDTO.class)))
                .thenReturn(updatedDTO);

        mockMvc.perform(put("/api/institutes/{uuid}", testUuid)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(testUuid.toString()))
                .andExpect(jsonPath("$.title").value("Обновленный институт"));

        verify(instituteService).updateInstitute(eq(testUuid), any(InstituteCreateUpdateDTO.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void deleteInstitute_WhenExists_ShouldReturnNoContent() throws Exception {
        doNothing().when(instituteService).deleteInstitute(testUuid);

        mockMvc.perform(delete("/api/institutes/{uuid}", testUuid)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(instituteService).deleteInstitute(testUuid);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void deleteInstitute_WhenNotExists_ShouldReturnNotFound() throws Exception {
        doThrow(new EntityNotFoundException("Institute not found with id: " + testUuid))
                .when(instituteService).deleteInstitute(testUuid);

        mockMvc.perform(delete("/api/institutes/{uuid}", testUuid)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("Institute not found")));

        verify(instituteService).deleteInstitute(testUuid);
    }

    @Test
    void createInstitute_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(post("/api/institutes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().is4xxClientError());

        verify(instituteService, never()).createInstitute(any());
    }
}