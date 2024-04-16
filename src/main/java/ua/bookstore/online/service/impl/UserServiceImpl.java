package ua.bookstore.online.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.bookstore.online.dto.user.UserRegistrationRequestDto;
import ua.bookstore.online.dto.user.UserResponseDto;
import ua.bookstore.online.exception.RegistrationException;
import ua.bookstore.online.mapper.UserMapper;
import ua.bookstore.online.model.User;
import ua.bookstore.online.repository.user.UserRepository;
import ua.bookstore.online.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto userRequestDto) throws
            RegistrationException {

        if (userRepository.findByEmail(userRequestDto.email()).isPresent()) {
            throw new RegistrationException(
                    "User already exist with email " + userRequestDto.email());
        }

        User user = userMapper.toModel(userRequestDto);
        user.setEmail(user.getEmail().toLowerCase());
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }
}
