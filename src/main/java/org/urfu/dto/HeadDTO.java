package org.urfu.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HeadDTO {
    private UUID uuid;

    @NotBlank(message = "ФИО ответственного лица обязательно")
    private String fullname;
}