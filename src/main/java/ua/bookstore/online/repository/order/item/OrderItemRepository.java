package ua.bookstore.online.repository.order.item;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ua.bookstore.online.model.OrderItem;
import ua.bookstore.online.model.User;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @Query("FROM OrderItem oi "
            + "JOIN FETCH oi.order o "
            + "JOIN FETCH o.user u "
            + "WHERE u = :user AND o.id = :orderId")
    List<OrderItem> fidAllByOrderAndUser(Long orderId, User user, Pageable pageable);

    @Query("FROM OrderItem oi "
            + "JOIN FETCH oi.order o "
            + "JOIN FETCH o.user u "
            + "WHERE u = :user AND o.id = :orderId AND oi.id = :id")
    Optional<OrderItem> findByIdAndOrder(Long id, Long orderId, User user);

}
