package ua.bookstore.online.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record UserLoginRequestDto(
        @NotBlank
        @Email(regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
        @Schema(example = "example@example.com")
        String email,
        @NotBlank
        @Length(min = 8, max = 24)
        @Schema(example = "passExample123")
        String password
) {
}
