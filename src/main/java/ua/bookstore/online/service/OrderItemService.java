package ua.bookstore.online.service;

import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Pageable;
import ua.bookstore.online.dto.order.OrderItemResponseDto;
import ua.bookstore.online.model.CartItem;
import ua.bookstore.online.model.OrderItem;
import ua.bookstore.online.model.User;

public interface OrderItemService {
    Set<OrderItem> convertFrom(Set<CartItem> cartItems);

    List<OrderItemResponseDto> findAllByOrder(Long orderId, User user, Pageable pageable);

    OrderItemResponseDto getById(Long id, Long orderId, User user);
}
