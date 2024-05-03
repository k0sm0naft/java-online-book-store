package ua.bookstore.online.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record OrderRequestDto(
        @NotBlank
        @Length(min = 3, max = 255)
        @Schema(example = "123 Main Street, Anytown, USA 12345")
        String shippingAddress
) {
}
