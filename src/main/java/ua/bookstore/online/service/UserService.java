package ua.bookstore.online.service;

import ua.bookstore.online.dto.user.UserRegistrationRequestDto;
import ua.bookstore.online.dto.user.UserResponseDto;
import ua.bookstore.online.exception.RegistrationException;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto userRequestDto) throws
            RegistrationException;
}
