package ua.bookstore.online.service;

import ua.bookstore.online.dto.shopping.cart.CartItemRequestDto;
import ua.bookstore.online.dto.shopping.cart.CartItemResponseDto;
import ua.bookstore.online.dto.shopping.cart.QuantityDto;
import ua.bookstore.online.model.ShoppingCart;

public interface CartItemService {
    CartItemResponseDto add(CartItemRequestDto cartItemRequestDto, ShoppingCart shoppingCart);

    QuantityDto changeQuantity(Long id, QuantityDto quantityDto, ShoppingCart shoppingCart);

    void remove(Long id, ShoppingCart shoppingCart);
}
