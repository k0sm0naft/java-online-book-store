package ua.bookstore.online.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;

public record CategoryResponseDto(
        @Schema(example = "12", nullable = true)
        Long id,
        @Schema(example = "Fiction", nullable = true)
        String name,
        @Schema(example = "Fiction typically involves imaginary characters...", nullable = true)
        String description
) {
}
