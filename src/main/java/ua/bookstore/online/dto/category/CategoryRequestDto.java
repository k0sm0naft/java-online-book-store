package ua.bookstore.online.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record CategoryRequestDto(
        @NotBlank
        @Length(max = 50)
        @Schema(example = "Fiction", nullable = true)
        String name,
        @NotBlank
        @Length(max = 255)
        @Schema(example = "Fiction typically involves imaginary characters...", nullable = true)
        String description
) {
}
