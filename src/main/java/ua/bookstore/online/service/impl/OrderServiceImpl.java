package ua.bookstore.online.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.bookstore.online.dto.order.OrderItemResponseDto;
import ua.bookstore.online.dto.order.OrderRequestDto;
import ua.bookstore.online.dto.order.OrderResponseDto;
import ua.bookstore.online.dto.order.StatusDto;
import ua.bookstore.online.exception.EntityNotFoundException;
import ua.bookstore.online.mapper.OrderMapper;
import ua.bookstore.online.model.CartItem;
import ua.bookstore.online.model.Order;
import ua.bookstore.online.model.ShoppingCart;
import ua.bookstore.online.model.User;
import ua.bookstore.online.repository.order.OrderRepository;
import ua.bookstore.online.repository.shopping.cart.ShoppingCartRepository;
import ua.bookstore.online.service.OrderItemService;
import ua.bookstore.online.service.OrderService;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final OrderMapper orderMapper;
    private final OrderItemService orderItemService;

    @Override
    @Transactional
    public OrderResponseDto saveOrder(OrderRequestDto requestDto, User user) {
        ShoppingCart shoppingCart = shoppingCartRepository.findShoppingCartByUser(user).orElseThrow(
                () -> new EntityNotFoundException(
                        "Can't find shopping cart from user: " + user.getEmail()));
        Set<CartItem> cartItems = shoppingCart.getCartItems();
        if (cartItems.isEmpty()) {
            throw new EntityNotFoundException("Can't find cartItems from user: " + user.getEmail());
        }

        shoppingCartRepository.deleteById(shoppingCart.getId());

        Order order = createOrder(requestDto, user, cartItems);
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toDto(savedOrder);
    }

    @Override
    @Transactional
    public List<OrderResponseDto> getAllOrders(User user, Pageable pageable) {
        return orderRepository.findAllByUser(user, pageable).stream()
                              .map(orderMapper::toDto)
                              .toList();
    }

    @Override
    @Transactional
    public void updateStatus(Long id, StatusDto statusDto) {
        if (orderRepository.updateStatusById(id, statusDto.status()) < 1) {
            throw new EntityNotFoundException("Can't find order by id: " + id);
        }
    }

    @Override
    @Transactional
    public List<OrderItemResponseDto> getAllCartItems(Long orderId, User user, Pageable pageable) {
        return orderItemService.findAllByOrder(orderId, user, pageable);
    }

    @Override
    @Transactional
    public OrderItemResponseDto getCartItem(Long id, Long orderId, User user) {
        return orderItemService.getById(id, orderId, user);
    }

    private BigDecimal getTotalPrice(User user, Set<CartItem> cartItems) {
        return cartItems.stream()
                        .map(cartItem -> cartItem.getBook().getPrice().multiply(
                                BigDecimal.valueOf(cartItem.getQuantity())))
                        .reduce(BigDecimal::add)
                        .orElseThrow(() -> new ArithmeticException(
                                "Can't calculate total sum for user order. User: "
                                        + user.getEmail()));
    }

    private Order createOrder(OrderRequestDto requestDto, User user, Set<CartItem> cartItems) {
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setTotal(getTotalPrice(user, cartItems));
        order.setShippingAddress(requestDto.shippingAddress());
        order.setStatus(Order.Status.PENDING);
        order.setOrderItems(orderItemService.createOrderItem(order, cartItems));
        return order;
    }
}
