package ua.bookstore.online.service.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ua.bookstore.online.dto.order.OrderItemResponseDto;
import ua.bookstore.online.exception.EntityNotFoundException;
import ua.bookstore.online.mapper.OrderItemMapper;
import ua.bookstore.online.model.CartItem;
import ua.bookstore.online.model.OrderItem;
import ua.bookstore.online.model.User;
import ua.bookstore.online.repository.order.item.OrderItemRepository;
import ua.bookstore.online.service.OrderItemService;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;

    @Override
    public Set<OrderItem> convertFrom(Set<CartItem> cartItems) {
        return cartItems.stream()
                        .map(orderItemMapper::toModel)
                        .collect(Collectors.toSet());
    }

    @Override
    public List<OrderItemResponseDto> findAllByOrder(Long orderId, User user, Pageable pageable) {
        List<OrderItem> orderItems =
                orderItemRepository.findAllByOrder_IdAndOrder_User(orderId, user, pageable);
        if (orderItems.isEmpty()) {
            throw new EntityNotFoundException("Can't find order by id: " + orderId);
        }
        return orderItems.stream()
                         .map(orderItemMapper::toDto)
                         .toList();
    }

    @Override
    public OrderItemResponseDto getById(Long id, Long orderId, User user) {
        OrderItem orderItem =
                orderItemRepository.findByIdAndOrder_IdAndOrder_User(id, orderId, user).orElseThrow(
                        () -> new EntityNotFoundException(
                                "Can't find order by id: %s and orderItem id: %s"
                                        .formatted(orderId, id)));
        return orderItemMapper.toDto(orderItem);
    }
}
