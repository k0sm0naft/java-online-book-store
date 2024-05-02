package ua.bookstore.online.repository.shopping.cart;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ua.bookstore.online.model.ShoppingCart;
import ua.bookstore.online.model.User;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    Optional<ShoppingCart> findByUser(User user);

    @Query("FROM ShoppingCart sc JOIN FETCH sc.cartItems WHERE sc.user = :user")
    Optional<ShoppingCart> findByUserWithCartItems(User user);
}
