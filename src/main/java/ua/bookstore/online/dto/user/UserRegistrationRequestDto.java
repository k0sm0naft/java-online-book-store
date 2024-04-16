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
        @Schema(example = "exemple@exemple.com")
        String email,
        @NotBlank
        @Length(min = 8, max = 24)
        @Schema(example = "passExample123")
        String password,
        @NotBlank
        @Length(min = 8, max = 24)
        @Schema(example = "passExample123")
        String validatePassword,
        @NotBlank
        @Length(min = 3, max = 24)
        @Pattern(regexp = "\\S*", message = "Field shouldn't include spaces")
        @Pattern(regexp = "[A-ZА-Я][a-zа-я]*",
                message = "Field should contain only first letter as capital")
        @Schema(example = "Example")
        String firstName,
        @NotBlank
        @Length(min = 3, max = 24)
        @Pattern(regexp = "\\S*", message = "Field shouldn't include spaces")
        @Pattern(regexp = "[A-ZА-Я][a-zа-я]*",
                message = "Field should contain only first letter as capital")
        @Schema(example = "Example")
        String lastName,
        @Schema(example = "123 Main Street, Anytown, USA 12345", nullable = true)
        String shippingAddress
) {
}
