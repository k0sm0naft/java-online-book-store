package ua.bookstore.online.service.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static ua.bookstore.online.utils.ConstantAndMethod.PASSWORD;
import static ua.bookstore.online.utils.ConstantAndMethod.getUser;
import static ua.bookstore.online.utils.ConstantAndMethod.getUserRequestDto;
import static ua.bookstore.online.utils.ConstantAndMethod.getUserResponseDto;
import static ua.bookstore.online.utils.ConstantAndMethod.getUserRole;

import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;
import ua.bookstore.online.dto.user.UserRegistrationRequestDto;
import ua.bookstore.online.dto.user.UserResponseDto;
import ua.bookstore.online.exception.RegistrationException;
import ua.bookstore.online.mapper.UserMapper;
import ua.bookstore.online.model.RoleName;
import ua.bookstore.online.model.User;
import ua.bookstore.online.repository.role.RoleRepository;
import ua.bookstore.online.repository.user.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserMapper userMapper;

    @AfterEach
    void afterEach() {
        // Verify method calls
        verifyNoMoreInteractions(userRepository, userMapper, passwordEncoder, roleRepository);
    }

    @Test
    @DisplayName("Register new user")
    void register_RegisterNewUser_ReturnsUserResponseDto() {
        // Given
        UserRegistrationRequestDto userRequestDto = getUserRequestDto();
        User user = getUser();
        UserResponseDto expected = getUserResponseDto();

        // Mocking behavior
        when(userRepository.findByEmail(userRequestDto.email())).thenReturn(Optional.empty());
        when(userMapper.toModel(userRequestDto)).thenReturn(user);
        when(passwordEncoder.encode(user.getPassword())).thenReturn(PASSWORD);
        when(roleRepository.getAllByNameIn(Set.of(RoleName.ROLE_USER))).thenReturn(
                Set.of(getUserRole()));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(expected);

        // When
        UserResponseDto actual = assertDoesNotThrow(() -> userService.register(userRequestDto));

        // Then
        assertNotNull(actual);
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
    }

    @Test
    @DisplayName("Register user whit non-unique email")
    void register_RegisterUserWithNonUniqueEmail_ThrowsException() {
        // Given
        UserRegistrationRequestDto userRequestDto = getUserRequestDto();

        // Mocking behavior
        when(userRepository.findByEmail(userRequestDto.email())).thenReturn(Optional.of(getUser()));

        // When
        Exception exception = assertThrows(
                RegistrationException.class, () -> userService.register(userRequestDto));

        // Then
        assertEquals("User already exist with email " + userRequestDto.email(),
                exception.getMessage());
    }
}
