package ua.bookstore.online.repository.order.item;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.bookstore.online.model.OrderItem;
import ua.bookstore.online.model.User;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @EntityGraph(attributePaths = {"book"})
    List<OrderItem> findAllByOrder_IdAndOrder_User(Long orderId, User user, Pageable pageable);

    @EntityGraph(attributePaths = {"book"})
    Optional<OrderItem> findByIdAndOrder_IdAndOrder_User(Long id, Long orderId, User user);

}
