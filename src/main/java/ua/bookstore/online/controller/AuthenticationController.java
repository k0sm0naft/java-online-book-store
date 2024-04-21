package ua.bookstore.online.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ua.bookstore.online.dto.ErrorResponseDto;
import ua.bookstore.online.dto.user.UserLoginRequestDto;
import ua.bookstore.online.dto.user.UserLoginResponseDto;
import ua.bookstore.online.dto.user.UserRegistrationRequestDto;
import ua.bookstore.online.dto.user.UserResponseDto;
import ua.bookstore.online.exception.RegistrationException;
import ua.bookstore.online.security.AuthenticationService;
import ua.bookstore.online.service.UserService;

@Tag(name = "Authentication management",
        description = "Endpoints for register and authenticate users")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/auth")
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register new user", description = "Register new user with unique email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully registered"),
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "Conflict - the email already exist",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
    })
    public UserResponseDto register(@RequestBody @Valid UserRegistrationRequestDto userRequestDto)
            throws RegistrationException {
        return userService.register(userRequestDto);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Login user", description = "Login user by email and password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Successfully login"),
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Credentials is wrong",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto userRequestDto) {
        return authenticationService.authenticate(userRequestDto);
    }
}
