package ua.bookstore.online.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static ua.bookstore.online.utils.ConstantAndMethod.ID_1;
import static ua.bookstore.online.utils.ConstantAndMethod.ID_2;
import static ua.bookstore.online.utils.ConstantAndMethod.NON_EXISTING_ID;
import static ua.bookstore.online.utils.ConstantAndMethod.getFirstOrder;
import static ua.bookstore.online.utils.ConstantAndMethod.getMalvilleOrderItem;
import static ua.bookstore.online.utils.ConstantAndMethod.getMalvilleOrderItemResponse;
import static ua.bookstore.online.utils.ConstantAndMethod.getUser;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ua.bookstore.online.dto.order.OrderItemResponseDto;
import ua.bookstore.online.exception.EntityNotFoundException;
import ua.bookstore.online.mapper.OrderItemMapper;
import ua.bookstore.online.model.OrderItem;
import ua.bookstore.online.model.User;
import ua.bookstore.online.repository.order.item.OrderItemRepository;

@ExtendWith(MockitoExtension.class)
class OrderItemServiceImplTest {
    @InjectMocks
    private OrderItemServiceImpl orderItemService;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private OrderItemMapper orderItemMapper;

    @AfterEach
    void afterEach() {
        // Verify method calls
        verifyNoMoreInteractions(orderItemRepository, orderItemMapper);
    }

    @Test
    @DisplayName("Get all order item DTOs")
    void findAllByOrder_GetAllOrderItems_ReturnsListOfOrderItemsDtos() {
        // Given
        User user = getUser();
        Pageable pageable = Pageable.unpaged();
        OrderItem malvilleOrderItem = getMalvilleOrderItem(ID_2, getFirstOrder());
        List<OrderItem> orderItems = List.of(malvilleOrderItem);
        OrderItemResponseDto responseDto = getMalvilleOrderItemResponse();
        List<OrderItemResponseDto> expected = List.of(responseDto);

        // Mocking behavior
        when(orderItemRepository.findAllByOrder_IdAndOrder_User(ID_1, user, pageable))
                .thenReturn(orderItems);
        when(orderItemMapper.toDto(malvilleOrderItem)).thenReturn(responseDto);

        // When
        List<OrderItemResponseDto> actual = orderItemService.findAllByOrder(ID_1, user, pageable);

        // Then
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Get all order item DTOs from non-existing order")
    void findAllByOrder_GetAllOrderItemsFromNonExistingOrder_ThrowsException() {
        // Given
        User user = getUser();
        Pageable pageable = Pageable.unpaged();

        // Mocking behavior
        when(orderItemRepository.findAllByOrder_IdAndOrder_User(NON_EXISTING_ID, user, pageable))
                .thenReturn(List.of());

        // When
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> orderItemService.findAllByOrder(NON_EXISTING_ID, user, pageable));

        // Then
        assertEquals("Can't find order by id: " + NON_EXISTING_ID, exception.getMessage());
    }

    @Test
    @DisplayName("Get order item DTO by ID")
    void getById_GetOrderItemByID_ReturnsOrderItemDto() {
        // Given
        User user = getUser();
        OrderItem cartItem = getMalvilleOrderItem(ID_2, getFirstOrder());
        OrderItemResponseDto expected = getMalvilleOrderItemResponse();

        // Mocking behavior
        when(orderItemRepository.findByIdAndOrder_IdAndOrder_User(ID_1, ID_1, user))
                .thenReturn(Optional.of(cartItem));
        when(orderItemMapper.toDto(cartItem)).thenReturn(expected);

        // When
        OrderItemResponseDto actual = orderItemService.getById(ID_1, ID_1, user);

        // Then
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Get order item DTO by non-existing ID")
    void getById_GetOrderItemByNonExistingID_ThrowException() {
        // Given
        User user = getUser();

        // Mocking behavior
        when(orderItemRepository.findByIdAndOrder_IdAndOrder_User(NON_EXISTING_ID, ID_1, user))
                .thenReturn(Optional.empty());

        // When
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> orderItemService.getById(NON_EXISTING_ID, ID_1, user));

        // Then
        assertEquals("Can't find order by id: %s and orderItem id: %s"
                .formatted(ID_1, NON_EXISTING_ID), exception.getMessage());
    }
}
