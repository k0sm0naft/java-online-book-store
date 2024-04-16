package ua.bookstore.online.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserResponseDto(
        @Schema(example = "123")
        Long id,
        @Schema(example = "example@example.com")
        String email,
        @Schema(example = "Example")
        String firstName,
        @Schema(example = "Example")
        String lastName,
        @Schema(example = "123 Main Street, Anytown, USA 12345")
        String shippingAddress
) {
}
