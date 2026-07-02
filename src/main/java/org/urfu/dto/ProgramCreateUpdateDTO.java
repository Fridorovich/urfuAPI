package org.urfu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgramCreateUpdateDTO {
    @NotBlank(message = "Название обязательно")
    private String title;

    @NotBlank(message = "Шифр обязателен")
    private String cypher;

    @NotBlank(message = "Уровень обязателен")
    private String level;

    @NotBlank(message = "Стандарт обязателен")
    private String standard;

    @NotNull(message = "Институт обязателен")
    private UUID instituteUuid;

    @NotNull(message = "Ответственное лицо обязательно")
    private UUID headUuid;

    @NotNull(message = "Дата аккредитации обязательна")
    private LocalDate accreditationDate;

    private List<UUID> moduleUuids;
}