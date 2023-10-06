package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.user.dto.UserShortDto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventShortDto {
    private Long id;
    @NotNull
    private String annotation;
    @NotNull
    private CategoryDto category;
    @PositiveOrZero
    private Long confirmedRequests;
    @NotNull
    private String title;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    @NotNull
    private UserShortDto initiator;
    @NotNull
    private Boolean paid;
    private Long views;
}
