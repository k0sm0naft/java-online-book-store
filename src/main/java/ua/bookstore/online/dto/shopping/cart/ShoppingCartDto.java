package ua.bookstore.online.dto.shopping.cart;

import java.util.List;

public record ShoppingCartDto(
        Long id,
        Long userId,
        List<CartItemResponseDto> cartItems
) {
}
