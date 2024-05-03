package ua.bookstore.online.dto.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import ua.bookstore.online.model.Order;

public record StatusDto(
        @JsonProperty(value = "status", required = true)
        Order.Status status
) {
}
