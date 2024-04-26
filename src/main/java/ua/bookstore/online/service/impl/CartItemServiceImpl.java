package ua.bookstore.online.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.bookstore.online.dto.shopping.cart.CartItemRequestDto;
import ua.bookstore.online.dto.shopping.cart.CartItemResponseDto;
import ua.bookstore.online.dto.shopping.cart.QuantityDto;
import ua.bookstore.online.exception.EntityNotFoundException;
import ua.bookstore.online.mapper.CartItemMapper;
import ua.bookstore.online.model.Book;
import ua.bookstore.online.model.CartItem;
import ua.bookstore.online.model.ShoppingCart;
import ua.bookstore.online.repository.book.BookRepository;
import ua.bookstore.online.repository.shopping.cart.item.CartItemRepository;
import ua.bookstore.online.service.CartItemService;

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {
    private final BookRepository bookRepository;
    private final CartItemRepository cartItemRepository;
    private final CartItemMapper cartItemMapper;

    @Override
    public CartItemResponseDto add(CartItemRequestDto requestDto, ShoppingCart shoppingCart) {
        Book book = bookRepository.findById(requestDto.bookId()).orElseThrow(
                () -> new EntityNotFoundException("Book not found by id " + requestDto.bookId()));

        CartItem cartItem = cartItemRepository.findByBookAndShoppingCart(book, shoppingCart)
                                               .map(item -> {
                                                   item.setQuantity(item.getQuantity());
                                                   return item;
                                               })
                                               .orElseGet(() -> {
                                                   CartItem newCartItem = new CartItem();
                                                   newCartItem.setQuantity(requestDto.quantity());
                                                   newCartItem.setShoppingCart(shoppingCart);
                                                   newCartItem.setBook(book);
                                                   return newCartItem;
                                               });
        return cartItemMapper.toDto(cartItemRepository.save(cartItem));
    }

    @Override
    public QuantityDto changeQuantity(Long id, QuantityDto quantity, ShoppingCart shoppingCart) {
        CartItem cartItem = cartItemRepository
                .findCartItemByIdAndShoppingCart(id, shoppingCart)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find cart item id%s for user %s"
                                .formatted(id, shoppingCart.getUser().getEmail())));
        cartItem.setQuantity(quantity.quantity());
        CartItem updatedCartItem = cartItemRepository.save(cartItem);
        return new QuantityDto(updatedCartItem.getQuantity());
    }

    @Override
    public void remove(Long id, ShoppingCart shoppingCart) {
        if (!cartItemRepository.existsByIdAndShoppingCart(id, shoppingCart)) {
            throw new EntityNotFoundException("Can't find cart item id%s for user %s".formatted(id,
                    shoppingCart.getUser().getEmail()));
        }
        cartItemRepository.deleteByIdAndShoppingCart(id, shoppingCart);
    }
}
