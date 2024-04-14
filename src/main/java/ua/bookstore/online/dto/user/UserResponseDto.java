package ua.bookstore.online.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserResponseDto(
        @Schema(description = "Users id", example = "123")
        Long id,
        @Schema(description = "Users email", example = "example@example.com")
        String email,
        @Schema(description = "Users first name", example = "Example")
        String firstName,
        @Schema(description = "Users last name", example = "Example")
        String lastName,
        @Schema(description = "Users shipping address",
                example = "123 Main Street, Anytown, USA 12345")
        String shippingAddress
) {
}
