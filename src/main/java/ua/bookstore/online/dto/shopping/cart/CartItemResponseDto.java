package ua.bookstore.online.dto.shopping.cart;

import io.swagger.v3.oas.annotations.media.Schema;

public record CartItemResponseDto(
        @Schema(example = "843", nullable = true)
        Long id,
        @Schema(example = "541", nullable = true)
        Long bookId,
        @Schema(example = "Example", nullable = true)
        String bookTitle,
        @Schema(example = "5", nullable = true)
        int quantity
) {
}
