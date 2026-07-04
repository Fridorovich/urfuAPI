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
import org.urfu.dto.HeadCreateUpdateDTO;
import org.urfu.dto.HeadDTO;
import org.urfu.service.HeadService;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HeadController.class)
class HeadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private HeadService headService;

    private UUID testUuid;
    private HeadDTO testHeadDTO;
    private HeadCreateUpdateDTO createDTO;

    @BeforeEach
    void setUp() {
        testUuid = UUID.randomUUID();
        testHeadDTO = HeadDTO.builder()
                .uuid(testUuid)
                .fullname("Иванов Иван Иванович")
                .build();

        createDTO = HeadCreateUpdateDTO.builder()
                .fullname("Петров Петр Петрович")
                .build();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void createHead_ShouldReturnCreatedHead() throws Exception {
        when(headService.createHead(any(HeadCreateUpdateDTO.class)))
                .thenReturn(testHeadDTO);

        mockMvc.perform(post("/api/heads")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.uuid").value(testUuid.toString()))
                .andExpect(jsonPath("$.fullname").value("Иванов Иван Иванович"));

        verify(headService).createHead(any(HeadCreateUpdateDTO.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void createHead_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        HeadCreateUpdateDTO invalidDTO = HeadCreateUpdateDTO.builder()
                .fullname("")
                .build();

        mockMvc.perform(post("/api/heads")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(headService, never()).createHead(any());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void getAllHeads_ShouldReturnList() throws Exception {
        List<HeadDTO> heads = List.of(testHeadDTO);
        when(headService.getAllHeads()).thenReturn(heads);

        mockMvc.perform(get("/api/heads"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].uuid").value(testUuid.toString()))
                .andExpect(jsonPath("$[0].fullname").value("Иванов Иван Иванович"));

        verify(headService).getAllHeads();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void getHeadById_WhenExists_ShouldReturnHead() throws Exception {
        when(headService.getHeadById(testUuid)).thenReturn(testHeadDTO);

        mockMvc.perform(get("/api/heads/{uuid}", testUuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(testUuid.toString()))
                .andExpect(jsonPath("$.fullname").value("Иванов Иван Иванович"));

        verify(headService).getHeadById(testUuid);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void getHeadById_WhenNotExists_ShouldReturnNotFound() throws Exception {
        when(headService.getHeadById(testUuid))
                .thenThrow(new EntityNotFoundException("Head not found with id: " + testUuid));

        mockMvc.perform(get("/api/heads/{uuid}", testUuid))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("Head not found")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void updateHead_WhenExists_ShouldReturnUpdatedHead() throws Exception {
        HeadCreateUpdateDTO updateDTO = HeadCreateUpdateDTO.builder()
                .fullname("Сидоров Сидор Сидорович")
                .build();

        HeadDTO updatedDTO = HeadDTO.builder()
                .uuid(testUuid)
                .fullname("Сидоров Сидор Сидорович")
                .build();

        when(headService.updateHead(eq(testUuid), any(HeadCreateUpdateDTO.class)))
                .thenReturn(updatedDTO);

        mockMvc.perform(put("/api/heads/{uuid}", testUuid)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(testUuid.toString()))
                .andExpect(jsonPath("$.fullname").value("Сидоров Сидор Сидорович"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void deleteHead_WhenExists_ShouldReturnNoContent() throws Exception {
        doNothing().when(headService).deleteHead(testUuid);

        mockMvc.perform(delete("/api/heads/{uuid}", testUuid)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(headService).deleteHead(testUuid);
    }
}