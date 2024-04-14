package ua.bookstore.online.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;
import ua.bookstore.online.lib.FieldMatch;

@FieldMatch.List({
        @FieldMatch(field = "password",
                fieldMatch = "validatePassword",
                message = "Passwords don't match!")
})
public record UserRegistrationRequestDto(
        @NotBlank
        @Email(regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
        @Schema(description = "Users email", example = "exemple@exemple.com", nullable = true)
        String email,
        @NotBlank
        @Length(min = 8, max = 24)
        @Schema(description = "Users password", example = "passExample123", nullable = true)
        String password,
        @Schema(description = "Validation of user password", example = "passExample123",
                nullable = true)
        String validatePassword,
        @NotBlank
        @Length(min = 3, max = 24)
        @Pattern(regexp = "\\S*", message = "Field shouldn't include spaces")
        @Pattern(regexp = "[A-ZА-Я][a-zа-я]*",
                message = "Field should contain only first letter as capital")
        @Schema(description = "First name of user", example = "Example", nullable = true)
        String firstName,
        @NotBlank
        @Length(min = 3, max = 24)
        @Pattern(regexp = "\\S*", message = "Field shouldn't include spaces")
        @Pattern(regexp = "[A-ZА-Я][a-zа-я]*",
                message = "Field should contain only first letter as capital")
        @Schema(description = "Last name of user", example = "Example", nullable = true)
        String lastName,
        @Schema(description = "Shipping address of user",
                example = "123 Main Street, Anytown, USA 12345", nullable = true)
        String shippingAddress
) {
}
