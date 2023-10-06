package ru.practicum.user.service;

import ru.practicum.user.dto.NewUserRequestDto;
import ru.practicum.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(NewUserRequestDto newUserRequestDto);

    List<UserDto> getUsersByIds(List<Long> ids, Integer from, Integer size);

    void deleteUser(Long userId);
}
