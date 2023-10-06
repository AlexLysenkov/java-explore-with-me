package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.event.model.Event;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestStatus;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    Request findByRequesterIdAndEventId(Long userId, Long eventId);

    Long countByEventIdAndStatus(Long eventId, RequestStatus status);

    List<Request> findAllByRequesterId(Long userId);

    List<Request> findAllByIdIn(List<Long> requestIds);

    @Query("SELECT r FROM Request AS r " +
            "JOIN Event AS e ON r.event = e.id " +
            "WHERE r.event = :eventId AND e.initiator.id = :userId")
    List<Request> findAllByEventIdAndInitiatorId(@Param("userId") Long userId, @Param("eventId") Long eventId);

    List<Request> findAllByEventInAndStatus(List<Event> events, RequestStatus status);

    List<Request> findByEventIn(List<Event> events);
}
