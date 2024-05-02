package ua.bookstore.online.service;

import ua.bookstore.online.dto.shopping.cart.CartItemRequestDto;
import ua.bookstore.online.dto.shopping.cart.CartItemResponseDto;
import ua.bookstore.online.dto.shopping.cart.QuantityDto;
import ua.bookstore.online.dto.shopping.cart.ShoppingCartDto;
import ua.bookstore.online.model.User;

public interface ShoppingCartService {
    ShoppingCartDto getShoppingCartWithCartItems(User user);

    QuantityDto updateCartItem(Long cartItemId, QuantityDto quantityDto, User user);

    void removeCartItem(Long cartItemId, User user);

    CartItemResponseDto addCartItem(CartItemRequestDto requestDto, User user);
}
