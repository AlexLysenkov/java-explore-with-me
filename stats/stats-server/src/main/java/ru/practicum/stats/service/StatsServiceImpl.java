package ru.practicum.stats.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.stats.mapper.EndpointHitMapper;
import ru.practicum.stats.model.EndpointHit;
import ru.practicum.stats.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    @Override
    public EndpointHitDto createHit(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = statsRepository.save(EndpointHitMapper.toEndpointHit(endpointHitDto));
        log.info("Создан новый EndpointHit {}", endpointHitDto);
        return EndpointHitMapper.toEndpointHitDto(endpointHit);
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (start.isAfter(end)) {
            log.info("start не может быть позже end");
            throw new IllegalArgumentException("start не может быть позже end");
        }
        log.info("Получена статистика за период с {} по {}", start, end);
        if (unique) {
            if (uris == null || uris.isEmpty()) {
                return statsRepository.findUniqueIpStats(start, end);
            } else {
                return statsRepository.findUniqueIpStatsWithUris(start, end, uris);
            }
        } else {
            if (uris == null || uris.isEmpty()) {
                return statsRepository.findStatsByDate(start, end);
            } else {
                return statsRepository.findStatsWithUris(start, end, uris);
            }
        }
    }
}
