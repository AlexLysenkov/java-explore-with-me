package ru.practicum.user.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.user.dto.NewUserRequestDto;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class UserMapper {
    public UserDto userToDto(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User не может быть null");
        }
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public User dtoToUser(NewUserRequestDto newUserRequestDto) {
        if (newUserRequestDto == null) {
            throw new IllegalArgumentException("NewUserRequestDto не может быть null");
        }
        return User.builder()
                .name(newUserRequestDto.getName())
                .email(newUserRequestDto.getEmail())
                .build();
    }

    public UserShortDto userToShortDto(User user) {
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }

    public List<UserDto> listUsersToListDto(List<User> users) {
        return users.stream().map(UserMapper::userToDto).collect(Collectors.toList());
    }
}
