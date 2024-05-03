package ua.bookstore.online.service;

import java.util.List;
import org.springframework.data.domain.Pageable;
import ua.bookstore.online.dto.order.OrderItemResponseDto;
import ua.bookstore.online.dto.order.OrderRequestDto;
import ua.bookstore.online.dto.order.OrderResponseDto;
import ua.bookstore.online.dto.order.StatusDto;
import ua.bookstore.online.model.User;

public interface OrderService {
    OrderResponseDto saveOrder(OrderRequestDto requestDto, User user);

    List<OrderResponseDto> getAllOrders(User user, Pageable pageable);

    void updateStatus(Long id, StatusDto statusDto);

    List<OrderItemResponseDto> getAllCartItems(Long orderId, User user, Pageable pageable);

    OrderItemResponseDto getCartItem(Long id, Long orderId, User user);
}
