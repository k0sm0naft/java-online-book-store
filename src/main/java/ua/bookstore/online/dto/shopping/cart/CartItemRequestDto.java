package ua.bookstore.online.dto.shopping.cart;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

public record CartItemRequestDto(
        @NotEmpty
        @Schema(example = "465", nullable = true)
        Long bookId,
        @NotEmpty
        @Schema(example = "5", nullable = true)
        int quantity
) {
}
