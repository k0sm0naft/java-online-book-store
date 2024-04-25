package ua.bookstore.online.dto.category;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record CategoryRequestDto(
        @NotBlank
        @Length(max = 50)
        String name,
        @NotBlank
        @Length(max = 255)
        String description
) {
}
