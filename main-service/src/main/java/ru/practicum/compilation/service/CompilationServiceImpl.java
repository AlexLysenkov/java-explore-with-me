package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.utils.CustomPageRequest;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        if (newCompilationDto.getPinned() == null) {
            newCompilationDto.setPinned(false);
        }
        Compilation compilation = CompilationMapper.dtoToCompilation(newCompilationDto);
        if (newCompilationDto.getEvents() != null) {
            compilation.setEvents(eventRepository.findEventsByIdIn(newCompilationDto.getEvents()));
        }
        log.info("Compilation {} создана", newCompilationDto);
        return CompilationMapper.compilationToDto(compilationRepository.save(compilation));
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Compilation с id: %d не найдена", compId)));
        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }
        if (updateCompilationRequest.getTitle() != null) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }
        if (updateCompilationRequest.getEvents() != null) {
            compilation.setEvents(eventRepository.findEventsByIdIn(updateCompilationRequest.getEvents()));
        }
        log.info("Compilation с id {} обновлена на {}", compId, updateCompilationRequest);
        return CompilationMapper.compilationToDto(compilation);
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Compilation с id: %d не найдена", compId)));
        log.info("Compilation с id {} получена", compId);
        return CompilationMapper.compilationToDto(compilation);
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        checkCompilationExists(compId);
        log.info("Compilation с id {} удалена", compId);
        compilationRepository.deleteById(compId);
    }

    @Override
    public List<CompilationDto> getAllByPinned(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = new CustomPageRequest(from / size, size);
        List<Compilation> compilations;
        if (pinned != null) {
            compilations = compilationRepository.findAllByPinned(pinned, pageable);
        } else {
            compilations = compilationRepository.findAll(pageable).toList();
        }
        List<CompilationDto> result = compilations.stream().map(CompilationMapper::compilationToDto)
                .collect(Collectors.toList());
        log.info("Получены compilations с параметрами pinned={}, from={}, size={}", pinned, from, size);
        return result;
    }

    private void checkCompilationExists(Long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new ObjectNotFoundException(String.format("Compilation с id: %d не найдена", compId));
        }
    }
}
