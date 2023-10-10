package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.request.dto.EventRequestStatusUpdateRequestDto;
import ru.practicum.request.dto.EventRequestStatusUpdateResultDto;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private static final String USER_NOT_FOUND = "User с id: %d не найден";
    private static final String EVENT_NOT_FOUND = "Event с id: %d не найден";
    private static final String LIMIT = "Лимит на участников достигнут";

    @Override
    @Transactional
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new ObjectNotFoundException(String.format(USER_NOT_FOUND, userId)));
        Event event = eventRepository.lockById(eventId).orElseThrow(() ->
                new ObjectNotFoundException(String.format(EVENT_NOT_FOUND, eventId)));
        if (requestRepository.existsById(userId) && requestRepository.existsById(eventId)) {
            throw new ConflictException("Повторная заявка");
        }
        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Нельзя добавить заявку на участие в своем событии");
        }
        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Нельзя добавить заявку на участие в неопубликованном событии");
        }
        if (event.getParticipantLimit() <= getConfirmedCount(eventId) && event.getParticipantLimit() != 0) {
            throw new ConflictException(LIMIT);
        }
        Request request = Request.builder()
                .event(event)
                .requester(user)
                .created(LocalDateTime.now())
                .build();
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
        } else {
            request.setStatus(RequestStatus.PENDING);
        }
        log.info("Request с id {} создан", request.getId());
        return RequestMapper.requestToDto(requestRepository.save(request));
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        checkUserExistsById(userId);
        Request request = requestRepository.findById(requestId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Request с id: %d не найден", requestId)));
        if (!request.getRequester().getId().equals(userId)) {
            throw new ObjectNotFoundException("Отказаться может только пользователь создавший запрос");
        }
        request.setStatus(RequestStatus.CANCELED);
        Request newRequest = requestRepository.save(request);
        log.info("User с id {} отменил Request с id {}", userId, requestId);
        return RequestMapper.requestToDto(newRequest);
    }

    @Override
    public List<ParticipationRequestDto> getRequestsByUserId(Long userId) {
        checkUserExistsById(userId);
        log.info("Получены Requests, созданные user с id {}", userId);
        return RequestMapper.listParticipationToDto(requestRepository.findAllByRequesterId(userId));
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResultDto updateRequestStatus(Long userId, Long eventId,
                                                                 EventRequestStatusUpdateRequestDto request) {
        checkUserExistsById(userId);
        Event event = eventRepository.lockById(eventId).orElseThrow(() ->
                new ObjectNotFoundException(String.format(EVENT_NOT_FOUND, eventId)));
        List<Request> requestList = requestRepository.findAllByIdIn(request.getRequestIds());
        EventRequestStatusUpdateResultDto result = EventRequestStatusUpdateResultDto.builder().build();
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            return result;
        }
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Неверный пользователь");
        }
        if (requestList.stream().anyMatch(req -> req.getStatus() != RequestStatus.PENDING)) {
            throw new ConflictException("Нельзя изменить запрос");
        }
        if (request.getStatus() != RequestStatus.CONFIRMED && request.getStatus() != RequestStatus.REJECTED) {
            throw new ConflictException("Неверный запрос");
        }
        long freeSeats = event.getParticipantLimit() - getConfirmedCount(eventId);
        if (freeSeats == 0) {
            throw new ConflictException(LIMIT);
        }
        if (freeSeats >= request.getRequestIds().size() || request.getStatus() == RequestStatus.REJECTED) {
            requestList.forEach(req -> req.setStatus(request.getStatus()));
        } else {
            requestList.forEach(req -> req.setStatus(RequestStatus.REJECTED));
            requestList.stream().limit(freeSeats).forEach(req -> req.setStatus(RequestStatus.CONFIRMED));
        }
        requestRepository.saveAll(requestList);
        List<Request> confirmedRequests = new ArrayList<>();
        List<Request> rejectedRequests = new ArrayList<>();
        for (Request newRequest : requestList) {
            if (newRequest.getStatus() == RequestStatus.CONFIRMED) {
                confirmedRequests.add(newRequest);
            } else {
                rejectedRequests.add(newRequest);
            }
        }
        result.setConfirmedRequests(RequestMapper.listParticipationToDto(confirmedRequests));
        result.setRejectedRequests(RequestMapper.listParticipationToDto(rejectedRequests));
        log.info("Обновлен Request {} для user с id {}, event с id {}.", request, userId, eventId);
        requestRepository.saveAll(requestList);
        return result;
    }

    @Override
    public List<ParticipationRequestDto> getRequestsByOwner(Long userId, Long eventId) {
        checkUserExistsById(userId);
        List<Event> events = eventRepository.findByIdAndInitiatorId(eventId, userId);
        List<Request> requestsDto = requestRepository.findByEventIn(events);
        log.info("Получены Requests для Initiator с id {} для event с id {}.", userId, eventId);
        return RequestMapper.listParticipationToDto(requestsDto);
    }

    private Long getConfirmedCount(Long eventId) {
        return requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
    }

    private void checkUserExistsById(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException(String.format(USER_NOT_FOUND, userId));
        }
    }
}
