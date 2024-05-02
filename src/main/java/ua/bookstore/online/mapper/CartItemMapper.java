package ua.bookstore.online.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.bookstore.online.config.MapperConfig;
import ua.bookstore.online.dto.shopping.cart.CartItemRequestDto;
import ua.bookstore.online.dto.shopping.cart.CartItemResponseDto;
import ua.bookstore.online.model.Book;
import ua.bookstore.online.model.CartItem;
import ua.bookstore.online.model.ShoppingCart;

@Mapper(config = MapperConfig.class)
public interface CartItemMapper {
    @Mapping(target = "bookId", source = "book.id")
    @Mapping(target = "bookTitle", source = "book.title")
    CartItemResponseDto toDto(CartItem cartItem);

    default CartItem getCartItem(
            CartItemRequestDto requestDto, ShoppingCart shoppingCart, Book book
    ) {
        CartItem newCartItem = new CartItem();
        newCartItem.setQuantity(requestDto.quantity());
        newCartItem.setShoppingCart(shoppingCart);
        newCartItem.setBook(book);
        return newCartItem;
    }
}
