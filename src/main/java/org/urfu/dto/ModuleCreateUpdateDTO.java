package org.urfu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleCreateUpdateDTO {
    @NotBlank(message = "Название модуля обязательно")
    private String title;

    @NotBlank(message = "Тип модуля обязателен")
    private String type;
}