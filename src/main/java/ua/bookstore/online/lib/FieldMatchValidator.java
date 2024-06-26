package ua.bookstore.online.lib;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Objects;
import ua.bookstore.online.dto.user.UserRegistrationRequestDto;

public class FieldMatchValidator
        implements ConstraintValidator<FieldMatch, UserRegistrationRequestDto> {
    @Override
    public boolean isValid(UserRegistrationRequestDto userDto, ConstraintValidatorContext context) {
        return Objects.equals(userDto.password(), userDto.validatePassword());
    }
}
