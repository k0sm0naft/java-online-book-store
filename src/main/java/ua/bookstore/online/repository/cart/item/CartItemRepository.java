package ua.bookstore.online.repository.cart.item;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.bookstore.online.model.Book;
import ua.bookstore.online.model.CartItem;
import ua.bookstore.online.model.ShoppingCart;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findCartItemByIdAndShoppingCart(Long id, ShoppingCart shoppingCart);

    Optional<CartItem> findByBookAndShoppingCart(Book book, ShoppingCart shoppingCart);

    boolean existsByIdAndShoppingCart(Long cartItemId, ShoppingCart shoppingCart);

    void deleteByIdAndShoppingCart(Long cartItemId, ShoppingCart shoppingCart);
}
