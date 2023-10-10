package ru.practicum.request.service;

import ru.practicum.request.dto.EventRequestStatusUpdateRequestDto;
import ru.practicum.request.dto.EventRequestStatusUpdateResultDto;
import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    ParticipationRequestDto createRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);

    List<ParticipationRequestDto> getRequestsByUserId(Long userId);

    EventRequestStatusUpdateResultDto updateRequestStatus(Long userId, Long eventId,
                                                          EventRequestStatusUpdateRequestDto request);

    List<ParticipationRequestDto> getRequestsByOwner(Long userId, Long eventId);
}
