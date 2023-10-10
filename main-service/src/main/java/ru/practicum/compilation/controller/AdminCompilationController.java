package ru.practicum.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.service.CompilationService;

import javax.validation.Valid;

@Controller
@Validated
@Slf4j
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
public class AdminCompilationController {
    private final CompilationService compilationService;

    @PostMapping
    public ResponseEntity<CompilationDto> createCompilation(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        log.info("Получен POST запрос по эндпоинту '/admin/compilations' на добавление Compilation {}",
                newCompilationDto);
        return new ResponseEntity<>(compilationService.createCompilation(newCompilationDto), HttpStatus.CREATED);
    }

    @PatchMapping("/{compId}")
    public ResponseEntity<CompilationDto> updateCompilation(@PathVariable Long compId,
                                                            @Valid @RequestBody
                                                            UpdateCompilationRequest updateCompilationRequest) {
        log.info("Получен PATCH запрос по эндпоинту '/admin/compilations/{}' на обновление compilation на {}",
                compId, updateCompilationRequest);
        return ResponseEntity.ok(compilationService.updateCompilation(compId, updateCompilationRequest));
    }

    @DeleteMapping("/{compId}")
    public ResponseEntity<?> deleteCompilation(@PathVariable Long compId) {
        compilationService.deleteCompilation(compId);
        log.info("Получен DELETE запрос по эндпоинту '/admin/compilations/{}' на удаление Compilation с id {}", compId,
                compId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
