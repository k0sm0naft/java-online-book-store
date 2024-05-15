package ua.bookstore.online.service.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static ua.bookstore.online.utils.ConstantAndMethod.ID_1;
import static ua.bookstore.online.utils.ConstantAndMethod.getCartItemRequestDto;
import static ua.bookstore.online.utils.ConstantAndMethod.getCartItemResponseDto;
import static ua.bookstore.online.utils.ConstantAndMethod.getEmptyShoppingCartDto;
import static ua.bookstore.online.utils.ConstantAndMethod.getQuantityDto;
import static ua.bookstore.online.utils.ConstantAndMethod.getShoppingCart;
import static ua.bookstore.online.utils.ConstantAndMethod.getShoppingCartDto;
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
import ua.bookstore.online.dto.shopping.cart.CartItemRequestDto;
import ua.bookstore.online.dto.shopping.cart.CartItemResponseDto;
import ua.bookstore.online.dto.shopping.cart.QuantityDto;
import ua.bookstore.online.dto.shopping.cart.ShoppingCartDto;
import ua.bookstore.online.exception.EntityNotFoundException;
import ua.bookstore.online.mapper.ShoppingCartMapper;
import ua.bookstore.online.model.ShoppingCart;
import ua.bookstore.online.model.User;
import ua.bookstore.online.repository.shopping.cart.ShoppingCartRepository;
import ua.bookstore.online.service.CartItemService;

@ExtendWith(MockitoExtension.class)
class ShoppingCartServiceImplTest {
    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;

    @Mock
    private ShoppingCartRepository cartRepository;

    @Mock
    private ShoppingCartMapper cartMapper;

    @Mock
    private CartItemService cartItemService;

    @AfterEach
    void afterEach() {
        // Verify method calls
        verifyNoMoreInteractions(cartRepository, cartMapper, cartItemService);
    }

    @Test
    @DisplayName("Get shopping cart with cart items by user")
    void getShoppingCartWithCartItems_GetAllUsersCartItemsReturnsShoppingCartDto() {
        // Given
        User user = getUser();
        ShoppingCart shoppingCart = new ShoppingCart(user);
        ShoppingCartDto expected = getShoppingCartDto(List.of(getCartItemResponseDto()));

        // Mocking behavior
        when(cartRepository.findByUserWithCartItems(user)).thenReturn(Optional.of(shoppingCart));
        when(cartMapper.toDto(shoppingCart)).thenReturn(expected);

        // When
        ShoppingCartDto actual = shoppingCartService.getShoppingCartWithCartItems(user);

        // Then
        assertNotNull(actual);
        assertEquals(expected.cartItems().size(), actual.cartItems().size());
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Get shopping cart with cart items for new user")
    void getShoppingCartWithCartItems_NewUser_ReturnsNewShoppingCartDto() {
        // Given
        User user = getUser();
        ShoppingCart shoppingCartFromDB = new ShoppingCart(user);
        shoppingCartFromDB.setId(ID_1);
        ShoppingCartDto expected = getEmptyShoppingCartDto();

        // Mocking behavior
        when(cartRepository.findByUserWithCartItems(user)).thenReturn(Optional.empty());
        when(cartRepository.save(any(ShoppingCart.class))).thenReturn(shoppingCartFromDB);
        when(cartMapper.toDto(any(ShoppingCart.class))).thenReturn(expected);

        // When
        ShoppingCartDto actual = shoppingCartService.getShoppingCartWithCartItems(user);

        // Then
        assertNotNull(actual);
        assertTrue(actual.cartItems().isEmpty());
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Add cart item to existing shopping cart")
    void addCartItem_AddToExistingCart_ReturnsCartItemResponseDto() {
        // Given
        CartItemRequestDto requestDto = getCartItemRequestDto();
        User user = getUser();
        ShoppingCart shoppingCart = getShoppingCart();
        CartItemResponseDto expected = getCartItemResponseDto();

        // Mocking behavior
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(shoppingCart));
        when(cartItemService.add(requestDto, shoppingCart)).thenReturn(expected);

        // When
        CartItemResponseDto actual = shoppingCartService.addCartItem(requestDto, user);

        // Then
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Add cart item to non-existing shopping cart")
    void addCartItem_AddToNonExistingCart_ReturnsCartItemResponseDto() {
        // Given
        CartItemRequestDto requestDto = getCartItemRequestDto();
        User user = getUser();
        ShoppingCart shoppingCart = getShoppingCart();
        CartItemResponseDto expected = getCartItemResponseDto();

        // Mocking behavior
        when(cartRepository.findByUser(user)).thenReturn(Optional.empty());
        when(cartRepository.save(any(ShoppingCart.class))).thenReturn(shoppingCart);
        when(cartItemService.add(requestDto, shoppingCart)).thenReturn(expected);

        // When
        CartItemResponseDto actual = shoppingCartService.addCartItem(requestDto, user);

        // Then
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Update cart item in existing shopping cart")
    void updateCartItem_UpdateItemInExistingCart_ReturnsQuantityDto() {
        // Given
        QuantityDto quantityDto = getQuantityDto();
        User user = getUser();
        ShoppingCart shoppingCart = getShoppingCart();

        // Mocking behavior
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(shoppingCart));
        when(cartItemService.changeQuantity(ID_1, quantityDto, shoppingCart))
                .thenReturn(quantityDto);

        // When
        QuantityDto actual = shoppingCartService.updateCartItem(ID_1, quantityDto, user);

        // Then
        assertNotNull(actual);
        assertEquals(quantityDto, actual);
    }

    @Test
    @DisplayName("Update cart item in non-existing shopping cart")
    void updateCartItem_UpdateItemInNonExistingCart_ReturnsQuantityDto() {
        // Given
        QuantityDto quantityDto = getQuantityDto();
        User user = getUser();

        // Mocking behavior
        when(cartRepository.findByUser(user)).thenReturn(Optional.empty());

        // When
        Exception actual =
                assertThrows(EntityNotFoundException.class,
                        () -> shoppingCartService.updateCartItem(ID_1, quantityDto, user));

        // Then
        assertEquals("Can't update item in an empty cart", actual.getMessage());
    }

    @Test
    @DisplayName("Remove cart item from shopping cart")
    void removeCartItem_RemoveCartItemFromCart_SuccessfullyRemovesCartItem() {
        // Given
        User user = getUser();
        ShoppingCart shoppingCart = getShoppingCart();

        // Mocking behavior
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(shoppingCart));
        doNothing().when(cartItemService).remove(ID_1, shoppingCart);

        // When
        assertDoesNotThrow(() -> shoppingCartService.removeCartItem(ID_1, user));
    }

    @Test
    @DisplayName("Remove cart item from non-existing shopping cart")
    void removeCartItem_RemoveCartItemFromNonExistingCart_ThrowsException() {
        // Given
        User user = getUser();

        // Mocking behavior
        when(cartRepository.findByUser(user)).thenReturn(Optional.empty());

        // When
        Exception exception =
                assertThrows(EntityNotFoundException.class,
                        () -> shoppingCartService.removeCartItem(ID_1, user));

        // Then
        assertEquals("Can't remove item from empty cart", exception.getMessage());
    }
}
