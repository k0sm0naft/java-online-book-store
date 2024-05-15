package ua.bookstore.online.service.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static ua.bookstore.online.utils.ConstantAndMethod.ID_1;
import static ua.bookstore.online.utils.ConstantAndMethod.getCartItem;
import static ua.bookstore.online.utils.ConstantAndMethod.getCartItemRequestDto;
import static ua.bookstore.online.utils.ConstantAndMethod.getCartItemResponseDto;
import static ua.bookstore.online.utils.ConstantAndMethod.getOrwellBook;
import static ua.bookstore.online.utils.ConstantAndMethod.getQuantityDto;
import static ua.bookstore.online.utils.ConstantAndMethod.getShoppingCart;
import static ua.bookstore.online.utils.ConstantAndMethod.getUser;

import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.bookstore.online.dto.shopping.cart.CartItemRequestDto;
import ua.bookstore.online.dto.shopping.cart.CartItemResponseDto;
import ua.bookstore.online.dto.shopping.cart.QuantityDto;
import ua.bookstore.online.exception.EntityNotFoundException;
import ua.bookstore.online.mapper.CartItemMapper;
import ua.bookstore.online.model.Book;
import ua.bookstore.online.model.CartItem;
import ua.bookstore.online.model.ShoppingCart;
import ua.bookstore.online.repository.shopping.cart.item.CartItemRepository;
import ua.bookstore.online.service.BookService;

@ExtendWith(MockitoExtension.class)
class CartItemServiceImplTest {
    @InjectMocks
    private CartItemServiceImpl cartItemService;

    @Mock
    private BookService bookService;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private CartItemMapper cartItemMapper;

    @AfterEach
    void afterEach() {
        // Verify method calls
        verifyNoMoreInteractions(bookService, cartItemRepository, cartItemMapper);
    }

    @Test
    @DisplayName("Add new cart item to shopping cart")
    void add_AddsNewCartItemToShoppingCart_ReturnsCartItemResponseDto() {
        // Given
        CartItemRequestDto requestDto = getCartItemRequestDto();
        ShoppingCart shoppingCart = getShoppingCart();
        Book book = getOrwellBook();
        CartItem cartItem = getCartItem();
        CartItemResponseDto expected = getCartItemResponseDto();

        // Mocking behavior
        when(bookService.getBook(ID_1)).thenReturn(book);
        when(cartItemRepository.findByBookAndShoppingCart(book, shoppingCart)).thenReturn(
                Optional.empty());
        when(cartItemMapper.toModel(requestDto, shoppingCart, book)).thenReturn(cartItem);
        when(cartItemRepository.save(cartItem)).thenReturn(cartItem);
        when(cartItemMapper.toDto(cartItem)).thenReturn(expected);

        // When
        CartItemResponseDto actual = cartItemService.add(requestDto, shoppingCart);

        // Then
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Add existing cart item to shopping cart")
    void add_AddsExistingCartItemToShoppingCart_ReturnsCartItemResponseDto() {
        // Given
        CartItemRequestDto requestDto = getCartItemRequestDto();
        ShoppingCart shoppingCart = getShoppingCart();
        Book book = getOrwellBook();
        CartItem cartItem = getCartItem();
        CartItemResponseDto expected = getCartItemResponseDto();

        // Mocking behavior
        when(bookService.getBook(ID_1)).thenReturn(book);
        when(cartItemRepository.findByBookAndShoppingCart(book, shoppingCart)).thenReturn(
                Optional.of(cartItem));
        when(cartItemRepository.save(cartItem)).thenReturn(cartItem);
        when(cartItemMapper.toDto(cartItem)).thenReturn(expected);

        // When
        CartItemResponseDto actual = cartItemService.add(requestDto, shoppingCart);

        // Then
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Update existing cart item quantity in shopping cart")
    void changeQuantity_UpdatesExistingCartItemQuantity_ReturnsQuantityDto() {
        // Given
        CartItem cartItem = getCartItem();
        ShoppingCart shoppingCart = getShoppingCart();
        shoppingCart.setCartItems(Set.of(cartItem));

        // Mocking behavior
        when(cartItemRepository.save(cartItem)).thenReturn(cartItem);

        // When
        QuantityDto actual = cartItemService.changeQuantity(ID_1, getQuantityDto(), shoppingCart);

        // Then
        assertNotNull(actual);
        assertEquals(getQuantityDto().quantity(), actual.quantity());
    }

    @Test
    @DisplayName("Update non-existing cart item quantity in shopping cart")
    void changeQuantity_UpdatesNonExistingCartItemQuantity_ReturnsQuantityDto() {
        // Given
        ShoppingCart shoppingCart = new ShoppingCart(getUser());

        // When
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> cartItemService.changeQuantity(ID_1, getQuantityDto(), shoppingCart));

        // Then
        assertEquals("Can't find cart item id%s for user %s"
                .formatted(ID_1, shoppingCart.getUser().getEmail()), exception.getMessage());
    }

    @Test
    @DisplayName("Remove cart item from shopping cart")
    void remove_RemovesCartItemFromShoppingCart_SuccessfullyRemoved() {
        // Given
        Long cartItemId = 1L;
        ShoppingCart shoppingCart = getShoppingCart();

        // Mocking behavior
        when(cartItemRepository.existsByIdAndShoppingCart(cartItemId, shoppingCart))
                .thenReturn(true);
        doNothing().when(cartItemRepository).deleteByIdAndShoppingCart(cartItemId, shoppingCart);

        // When
        assertDoesNotThrow(() -> cartItemService.remove(cartItemId, shoppingCart));
    }

    @Test
    @DisplayName("Remove non-existing cart item throws exception")
    void remove_RemoveNonExistingCartItem_ThrowsException() {
        // Given
        ShoppingCart shoppingCart = getShoppingCart();

        // Mocking behavior
        when(cartItemRepository.existsByIdAndShoppingCart(ID_1, shoppingCart)).thenReturn(
                false);

        // When
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> cartItemService.remove(ID_1, shoppingCart));

        // Then
        assertEquals("Can't find cart item id%s for user %s"
                .formatted(ID_1, shoppingCart.getUser().getEmail()), exception.getMessage());
    }
}
