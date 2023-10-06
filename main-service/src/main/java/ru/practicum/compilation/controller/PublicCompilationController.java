package ru.practicum.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.service.CompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Controller
@Validated
@Slf4j
@RequestMapping("/compilations")
@RequiredArgsConstructor
public class PublicCompilationController {
    private final CompilationService compilationService;

    @GetMapping("/{compId}")
    public ResponseEntity<CompilationDto> getCompilationById(@PathVariable Long compId) {
        log.info("Получен GET запрос по эндпоинту '/compilations/{}' на получение compilation по id {}", compId,
                compId);
        return ResponseEntity.ok(compilationService.getCompilationById(compId));
    }

    @GetMapping
    public ResponseEntity<List<CompilationDto>> getAllByPinned(@RequestParam(required = false) Boolean pinned,
                                                               @RequestParam(required = false, defaultValue = "0")
                                                               @PositiveOrZero Integer from,
                                                               @RequestParam(required = false, defaultValue = "10")
                                                               @Positive Integer size) {
        log.info("Получен GET запрос по эндпоинту '/compilations' на получение compilations с параметрами pinned = {}, " +
                "from = {}, size = {}", pinned, from, size);
        return ResponseEntity.ok(compilationService.getAllByPinned(pinned, from, size));
    }
}
