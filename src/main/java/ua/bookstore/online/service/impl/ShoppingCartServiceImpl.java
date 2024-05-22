package ua.bookstore.online.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import ua.bookstore.online.service.ShoppingCartService;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository cartRepository;
    private final ShoppingCartMapper cartMapper;
    private final CartItemService cartItemService;

    @Override
    @Transactional
    public ShoppingCartDto getShoppingCartWithCartItems(User user) {
        ShoppingCart shoppingCart =
                cartRepository.findByUserWithCartItems(user)
                              .orElseGet(() -> cartRepository.save(new ShoppingCart(user)));
        return cartMapper.toDto(shoppingCart);
    }

    @Override
    @Transactional
    public CartItemResponseDto addCartItem(CartItemRequestDto requestDto, User user) {
        ShoppingCart shoppingCart = getExistedOrNewCart(user);
        return cartItemService.add(requestDto, shoppingCart);
    }

    @Override
    @Transactional
    public QuantityDto updateCartItem(Long cartItemId, QuantityDto quantityDto, User user) {
        ShoppingCart shoppingCart = cartRepository.findByUser(user).orElseThrow(
                () -> new EntityNotFoundException("Can't update item by ID: " + cartItemId));
        return cartItemService.changeQuantity(cartItemId, quantityDto, shoppingCart);
    }

    @Override
    @Transactional
    public void removeCartItem(Long cartItemId, User user) {
        ShoppingCart shoppingCart = cartRepository.findByUser(user).orElseThrow(
                () -> new EntityNotFoundException("Can't remove item by ID: " + cartItemId));
        cartItemService.remove(cartItemId, shoppingCart);
    }

    private ShoppingCart getExistedOrNewCart(User user) {
        return cartRepository.findByUser(user)
                             .orElseGet(() -> cartRepository.save(
                                     new ShoppingCart(user)));
    }
}
