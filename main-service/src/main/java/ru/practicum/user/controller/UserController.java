package ru.practicum.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.dto.NewUserRequestDto;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Controller
@Validated
@Slf4j
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody NewUserRequestDto newUserRequestDto) {
        log.info("Получен POST запрос по эндпоинту '/admin/users' на добавление user {}", newUserRequestDto);
        return new ResponseEntity<>(userService.createUser(newUserRequestDto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getUsersByIds(@RequestParam(required = false) List<Long> ids,
                                                       @RequestParam(required = false, defaultValue = "0")
                                                       @PositiveOrZero Integer from,
                                                       @RequestParam(required = false, defaultValue = "10")
                                                       @Positive Integer size) {
        log.info("Получен GET запрос по эндпоинту '/admin/users' на получение users с параметрами " +
                "ids = {}, from = {}, size = {}", ids, from, size);
        return ResponseEntity.ok(userService.getUsersByIds(ids, from, size));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        log.info("Получен DELETE запрос по эндпоинту '/admin/users/{}' на удаление user по id {}", userId, userId);
        userService.deleteUser(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
