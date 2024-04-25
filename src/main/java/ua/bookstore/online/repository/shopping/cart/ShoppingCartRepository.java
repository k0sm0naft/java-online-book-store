package ua.bookstore.online.repository.shopping.cart;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.bookstore.online.model.ShoppingCart;
import ua.bookstore.online.model.User;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    Optional<ShoppingCart> findByUser(User user);

    @EntityGraph(attributePaths = "cartItems.book")
    Optional<ShoppingCart> findShoppingCartByUser(User user);
}
