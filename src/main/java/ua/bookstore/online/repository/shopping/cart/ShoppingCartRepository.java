package ua.bookstore.online.repository.shopping.cart;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ua.bookstore.online.model.ShoppingCart;
import ua.bookstore.online.model.User;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    @Query("FROM ShoppingCart sc "
            + "LEFT JOIN FETCH sc.user u "
            + "LEFT JOIN FETCH sc.cartItems "
            + "WHERE u = :user")
    Optional<ShoppingCart> findByUser(User user);

    @Query("FROM ShoppingCart sc "
            + "LEFT JOIN FETCH sc.user u "
            + "LEFT JOIN FETCH sc.cartItems ci "
            + "LEFT JOIN FETCH ci.book "
            + "WHERE u = :user")
    Optional<ShoppingCart> findByUserWithCartItems(User user);
}
