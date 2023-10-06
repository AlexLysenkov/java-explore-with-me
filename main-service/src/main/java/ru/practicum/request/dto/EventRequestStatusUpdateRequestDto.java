package ru.practicum.request.dto;

import lombok.*;
import ru.practicum.request.model.RequestStatus;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestStatusUpdateRequestDto {
    @NotNull
    @NotEmpty
    private List<Long> requestIds;
    @NotNull
    private RequestStatus status;
}
