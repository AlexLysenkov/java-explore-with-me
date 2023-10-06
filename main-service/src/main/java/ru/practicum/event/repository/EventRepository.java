package ru.practicum.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByIdAndInitiatorId(Long eventId, Long userId);

    List<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    @Query(value = "SELECT * FROM Events e " +
            "WHERE (:userId IS NULL or e.initiator_id IN (cast(cast(:userId AS TEXT) AS BIGINT))) " +
            "AND (:states IS NULL or e.state IN (cast(:states AS text))) " +
            "AND (:categories IS NULL or e.category_id IN (cast(cast(:categories AS TEXT) AS BIGINT))) " +
            "AND (cast(:rangeStart AS TIMESTAMP) IS NULL or e.event_date >= cast(:rangeStart AS TIMESTAMP)) " +
            "AND (cast(:rangeEnd AS TIMESTAMP) IS NULL or e.event_date < cast(:rangeEnd AS TIMESTAMP))",
            nativeQuery = true)
    List<Event> findEventsByAdmin(@Param("userId") List<Long> users, @Param("states") List<String> states,
                                  @Param("categories") List<Long> categories,
                                  @Param("rangeStart") LocalDateTime rangeStart,
                                  @Param("rangeEnd") LocalDateTime rangeEnd, Pageable pageable);

    @Query(value = "SELECT * FROM Events e WHERE (e.state = 'PUBLISHED') " +
            "AND (:text IS NULL OR lower(e.annotation) LIKE lower(concat('%',cast(:text AS text),'%')) " +
            "OR lower(e.description) LIKE lower(concat('%',cast(:text AS text),'%'))) " +
            "AND (:categories IS NULL OR e.category_id IN (cast(cast(:categories AS TEXT) AS BIGINT))) " +
            "AND (:paid IS NULL OR e.paid = cast(cast(:paid AS text) AS BOOLEAN)) " +
            "AND (e.event_date >= :rangeStart) " +
            "AND (cast(:rangeEnd AS TIMESTAMP) IS NULL OR e.event_date < cast(:rangeEnd AS TIMESTAMP))",
            nativeQuery = true)
    List<Event> findEventsByUser(@Param("text") String text, @Param("categories") List<Long> categories,
                                 @Param("paid") Boolean paid, @Param("rangeStart") LocalDateTime rangeStart,
                                 @Param("rangeEnd") LocalDateTime rangeEnd, Pageable pageable);

    Set<Event> findEventsByIdIn(Set<Long> ids);
}
