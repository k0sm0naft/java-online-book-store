package ua.bookstore.online.dto.shopping.cart;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record ShoppingCartDto(
        @Schema(example = "952", nullable = true)
        Long id,
        @Schema(example = "463", nullable = true)
        Long userId,
        @Schema(nullable = true)
        List<CartItemResponseDto> cartItems
) {
}
