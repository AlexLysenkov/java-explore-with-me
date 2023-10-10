package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.user.dto.NewUserRequestDto;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;
import ru.practicum.utils.CustomPageRequest;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto createUser(NewUserRequestDto newUserRequestDto) {
        User user = UserMapper.dtoToUser(newUserRequestDto);
        log.info("User с id: {} создан", user.getId());
        return UserMapper.userToDto(userRepository.save(user));
    }

    @Override
    public List<UserDto> getUsersByIds(List<Long> ids, Integer from, Integer size) {
        Pageable pageable = new CustomPageRequest(from, size);
        List<User> users;
        if (ids == null || ids.isEmpty()) {
            users = userRepository.findAll(pageable).toList();
        } else {
            users = userRepository.findAllByIdIn(ids, pageable);
        }
        log.info("Получен список всех Users с параметрами ids = {}, from = {}, size = {}", ids, from, size);
        return UserMapper.listUsersToListDto(users);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        checkUserExistsById(userId);
        userRepository.deleteById(userId);
        log.info("User с id: {} удален", userId);
    }

    private void checkUserExistsById(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException(String.format("User с id: %d не найден", userId));
        }
    }
}
