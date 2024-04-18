package ua.bookstore.online.service.impl;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.bookstore.online.dto.user.UserRegistrationRequestDto;
import ua.bookstore.online.dto.user.UserResponseDto;
import ua.bookstore.online.exception.RegistrationException;
import ua.bookstore.online.mapper.UserMapper;
import ua.bookstore.online.model.RoleName;
import ua.bookstore.online.model.User;
import ua.bookstore.online.repository.role.RoleRepository;
import ua.bookstore.online.repository.user.UserRepository;
import ua.bookstore.online.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Transactional
    @Override
    public UserResponseDto register(UserRegistrationRequestDto userRequestDto) throws
            RegistrationException {

        if (userRepository.findByEmail(userRequestDto.email()).isPresent()) {
            throw new RegistrationException(
                    "User already exist with email " + userRequestDto.email());
        }

        User user = userMapper.toModel(userRequestDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEmail(user.getEmail().toLowerCase());
        user.setRoles(roleRepository.getAllByNameIn(Set.of(RoleName.USER)));
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }
}
