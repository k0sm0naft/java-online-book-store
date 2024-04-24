package ua.bookstore.online.dto.shopping.cart;

import jakarta.validation.constraints.NotEmpty;

public record QuantityDto(
        @NotEmpty
        int quantity
) {
}
