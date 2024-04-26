package ua.bookstore.online.dto.order;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record OrderRequestDto(
        @NotBlank
        @Length(min = 3, max = 255)
        String shippingAddress
) {
}
