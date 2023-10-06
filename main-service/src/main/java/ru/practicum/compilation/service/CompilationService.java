package ru.practicum.compilation.service;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    CompilationDto createCompilation(NewCompilationDto newCompilationDto);

    CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest);

    CompilationDto getCompilationById(Long compId);

    void deleteCompilation(Long compId);

    List<CompilationDto> getAllByPinned(Boolean pinned, Integer from, Integer size);
}
