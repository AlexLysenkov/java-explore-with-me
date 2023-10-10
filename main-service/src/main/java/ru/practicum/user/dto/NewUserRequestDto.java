package ru.practicum.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewUserRequestDto {
    @Size(min = 2, max = 250)
    @NotBlank
    private String name;
    @Email
    @Size(min = 6, max = 254)
    @NotBlank
    private String email;
}
