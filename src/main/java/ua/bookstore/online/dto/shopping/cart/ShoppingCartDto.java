package ua.bookstore.online.dto.shopping.cart;

import java.util.Set;

public record ShoppingCartDto(
        Long id,
        Long userId,
        Set<CartItemResponseDto> cartItems
) {
}
