package ua.bookstore.online.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;

public record OrderItemResponseDto(
        @Schema(example = "864", nullable = true)
        Long id,
        @Schema(example = "225", nullable = true)
        Long bookId,
        @Schema(example = "5", nullable = true)
        int quantity
) {
}
