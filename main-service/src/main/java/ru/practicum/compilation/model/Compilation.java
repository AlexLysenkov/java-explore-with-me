package ru.practicum.compilation.model;

import lombok.*;
import ru.practicum.event.model.Event;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "compilations")
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "pinned", nullable = false)
    private Boolean pinned;
    @Column(name = "title", nullable = false, length = 50)
    private String title;
    @ManyToMany
    @JoinTable(name = "compilations_events",
            joinColumns = @JoinColumn(name = "compilation_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "event_id", referencedColumnName = "id"))
    private Set<Event> events;
}
