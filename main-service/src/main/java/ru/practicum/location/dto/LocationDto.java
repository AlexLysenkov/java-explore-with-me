package ru.practicum.location.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationDto {
    @NotNull
    private Float lat;
    @NotNull
    private Float lon;
}
