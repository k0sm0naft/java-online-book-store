package ua.bookstore.online.dto.shopping.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

public record QuantityDto(
        @NotEmpty
        @Min(value = 0)
        int quantity
) {
}
