package ua.bookstore.online.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponseDto(
        @Schema(example = "568", nullable = true)
        Long id,
        @Schema(example = "681", nullable = true)
        Long userId,
        @Schema(nullable = true)
        List<OrderItemResponseDto> orderItems,
        @Schema(nullable = true)
        LocalDateTime orderDate,
        @Schema(example = "165.28", nullable = true)
        BigDecimal total,
        @Schema(example = "DELIVERED", nullable = true)
        String status
) {
}
