package ua.bookstore.online.repository.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ua.bookstore.online.utils.ConstantAndMethod.ID_1;
import static ua.bookstore.online.utils.ConstantAndMethod.beforeEachOrderTest;
import static ua.bookstore.online.utils.ConstantAndMethod.getFirstOrder;
import static ua.bookstore.online.utils.ConstantAndMethod.getUser;
import static ua.bookstore.online.utils.ConstantAndMethod.tearDown;

import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ua.bookstore.online.model.Order;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OrderRepositoryTest {
    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    public void beforeEach(@Autowired DataSource dataSource) {
        beforeEachOrderTest(dataSource);
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        tearDown(dataSource);
    }

    @Test
    @DisplayName("Find all user's orders")
    void findByUser_FindingAllUsersOrders_ReturnsListOfUsersOrders() {
        // Given
        List<Order> expected = List.of(getFirstOrder());

        // When
        List<Order> actual = orderRepository.findAllByUser(getUser(), Pageable.unpaged());

        // Then
        assertEquals(expected.size(), actual.size());

        assertEquals(expected.getFirst().getId(), actual.getFirst().getId());
        assertEquals(expected.getFirst().getShippingAddress(),
                actual.getFirst().getShippingAddress());
        assertEquals(expected.getFirst().getUser().getId(), actual.getFirst().getUser().getId());
        assertEquals(expected.getFirst().getOrderItems().size(),
                actual.getFirst().getOrderItems().size());
    }

    @Test
    @DisplayName("Update order status by order ID")
    void updateStatusById_UpdateOrderStatusById_ReturnsOptionalOfUpdatedOrder() {
        // When
        int actual = orderRepository.updateStatusById(ID_1, Order.Status.PROCESSED);

        // Then
        assertEquals(1, actual, "Should be 1 modified row");
    }
}
