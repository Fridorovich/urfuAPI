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
public class ModuleDTO {
    private UUID uuid;

    @NotBlank(message = "Название модуля обязательно")
    private String title;

    @NotBlank(message = "Тип модуля обязателен")
    private String type;
}