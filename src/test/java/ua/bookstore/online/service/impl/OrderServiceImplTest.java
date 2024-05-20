package ua.bookstore.online.service.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static ua.bookstore.online.utils.TestDataUtils.ID_1;
import static ua.bookstore.online.utils.TestDataUtils.ID_2;
import static ua.bookstore.online.utils.TestDataUtils.NON_EXISTING_ID;
import static ua.bookstore.online.utils.TestDataUtils.SHIPPING_ADDRESS;
import static ua.bookstore.online.utils.TestDataUtils.getCartItem;
import static ua.bookstore.online.utils.TestDataUtils.getMalvilleOrderItem;
import static ua.bookstore.online.utils.TestDataUtils.getSecondOrder;
import static ua.bookstore.online.utils.TestDataUtils.getSecondOrderResponseDto;
import static ua.bookstore.online.utils.TestDataUtils.getShoppingCart;
import static ua.bookstore.online.utils.TestDataUtils.getUser;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ua.bookstore.online.dto.order.OrderRequestDto;
import ua.bookstore.online.dto.order.OrderResponseDto;
import ua.bookstore.online.dto.order.StatusDto;
import ua.bookstore.online.exception.EntityNotFoundException;
import ua.bookstore.online.mapper.OrderMapper;
import ua.bookstore.online.model.CartItem;
import ua.bookstore.online.model.Order;
import ua.bookstore.online.model.OrderItem;
import ua.bookstore.online.model.ShoppingCart;
import ua.bookstore.online.model.User;
import ua.bookstore.online.repository.order.OrderRepository;
import ua.bookstore.online.repository.shopping.cart.ShoppingCartRepository;
import ua.bookstore.online.service.OrderItemService;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {
    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemService orderItemService;

    @Mock
    private ShoppingCartRepository shoppingCartRepository;

    @Mock
    private OrderMapper orderMapper;

    @AfterEach
    void afterEach() {
        // Verify method calls
        verifyNoMoreInteractions(shoppingCartRepository, orderItemService, orderRepository,
                orderMapper);
    }

    @Test
    @DisplayName("Save order to DB from valid shopping cart, returns OrderResponseDto")
    void saveOrder_SaveOrderFromValidCart_ReturnsOrderDto() {
        // Given
        User user = getUser();
        ShoppingCart shoppingCart = getShoppingCart();
        Set<CartItem> cartItems = Set.of(getCartItem());
        shoppingCart.setCartItems(cartItems);
        OrderRequestDto requestDto = new OrderRequestDto(SHIPPING_ADDRESS);
        Order order = getSecondOrder();
        Set<OrderItem> orderItems = Set.of(getMalvilleOrderItem(ID_2, order));
        OrderResponseDto expected = getSecondOrderResponseDto();

        // Mocking behavior
        when(shoppingCartRepository.findByUserWithCartItems(user)).thenReturn(
                Optional.of(shoppingCart));
        doNothing().when(shoppingCartRepository).deleteById(any(Long.class));
        when(orderItemService.convertFrom(cartItems)).thenReturn(orderItems);
        when(orderMapper.toModel(requestDto, user, orderItems)).thenReturn(order);
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toDto(order)).thenReturn(expected);

        // When
        OrderResponseDto actual = orderService.saveOrder(requestDto, user);

        // Then
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Save order to DB from non-existing shopping cart, throws exception")
    void saveOrder_SaveOrderFromNonExistingCart_ThrowException() {
        // Given
        User user = getUser();
        OrderRequestDto requestDto = new OrderRequestDto(SHIPPING_ADDRESS);

        // Mocking behavior
        when(shoppingCartRepository.findByUserWithCartItems(user)).thenReturn(
                Optional.empty());

        // When
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> orderService.saveOrder(requestDto, user));

        // Then
        assertEquals("Can't find shopping cart from user: " + user.getEmail(),
                exception.getMessage());
    }

    @Test
    @DisplayName("Save order to DB from empty shopping cart, throws exception")
    void saveOrder_SaveOrderFromEmptyCart_ThrowException() {
        // Given
        User user = getUser();
        ShoppingCart shoppingCart = getShoppingCart();
        OrderRequestDto requestDto = new OrderRequestDto(SHIPPING_ADDRESS);

        // Mocking behavior
        when(shoppingCartRepository.findByUserWithCartItems(user)).thenReturn(
                Optional.of(shoppingCart));

        // When
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> orderService.saveOrder(requestDto, user));

        // Then
        assertEquals("Can't find cartItems from user: " + user.getEmail(), exception.getMessage());
    }

    @Test
    @DisplayName("Get all order item DTOs, returns list of OrderResponseDto")
    void getAllOrders_GetAllOrders_ReturnsListOfOrderDtos() {
        // Given
        User user = getUser();
        Pageable pageable = Pageable.unpaged();
        Order order = getSecondOrder();
        List<Order> orders = List.of(order);
        OrderResponseDto responseDto = getSecondOrderResponseDto();
        List<OrderResponseDto> expected = List.of(responseDto);

        // Mocking behavior
        when(orderRepository.findAllByUser(user, pageable)).thenReturn(orders);
        when(orderMapper.toDto(order)).thenReturn(responseDto);

        // When
        List<OrderResponseDto> actual = orderService.getAllOrders(user, pageable);

        // Then
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Update orders status, returns void")
    void updateStatus_UpdateStatus_SuccessfullyUpdated() {
        // Given
        StatusDto statusDto = new StatusDto(Order.Status.PROCESSED);

        // Mocking behavior
        when(orderRepository.updateStatusById(ID_1, statusDto.status())).thenReturn(1);

        // When / Then
        assertDoesNotThrow(() -> orderService.updateStatus(ID_1, statusDto));
    }

    @Test
    @DisplayName("Update status of non-existing order, throws exception")
    void updateStatus_UpdateStatusOfNonExistingOrder_ThrowsException() {
        // Given
        StatusDto statusDto = new StatusDto(Order.Status.PROCESSED);

        // Mocking behavior
        when(orderRepository.updateStatusById(NON_EXISTING_ID, statusDto.status())).thenReturn(0);

        // When
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> orderService.updateStatus(NON_EXISTING_ID, statusDto));

        //Then
        assertEquals("Can't find order by id: " + NON_EXISTING_ID, exception.getMessage());
    }
}
