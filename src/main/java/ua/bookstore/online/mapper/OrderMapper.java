package ua.bookstore.online.mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.bookstore.online.config.MapperConfig;
import ua.bookstore.online.dto.order.OrderRequestDto;
import ua.bookstore.online.dto.order.OrderResponseDto;
import ua.bookstore.online.model.Order;
import ua.bookstore.online.model.OrderItem;
import ua.bookstore.online.model.User;

@Mapper(config = MapperConfig.class, uses = OrderItemMapper.class)
public interface OrderMapper {
    @Mapping(target = "userId", source = "user.id")
    OrderResponseDto toDto(Order order);

    default Order toModel(OrderRequestDto requestDto, User user, Set<OrderItem> orderItems) {
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setTotal(getTotalPrice(user, orderItems));
        order.setShippingAddress(requestDto.shippingAddress());
        order.setStatus(Order.Status.PENDING);
        order.setOrderItems(orderItems.stream()
                                      .peek(orderItem -> orderItem.setOrder(order))
                                      .collect(Collectors.toSet()));
        return order;
    }

    private BigDecimal getTotalPrice(User user, Set<OrderItem> orderItems) {
        return orderItems.stream()
                         .map(cartItem -> cartItem.getBook().getPrice().multiply(
                                 BigDecimal.valueOf(cartItem.getQuantity())))
                         .reduce(BigDecimal::add)
                         .orElseThrow(() -> new ArithmeticException(
                                 "Can't calculate total sum for user order. User: "
                                         + user.getEmail()));
    }
}
