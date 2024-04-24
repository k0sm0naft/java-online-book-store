package ua.bookstore.online.dto.shopping.cart;

import jakarta.validation.constraints.NotEmpty;

public record CartItemRequestDto(
        @NotEmpty
        Long bookId,
        @NotEmpty
        int quantity
) {
}
