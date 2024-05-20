package ua.bookstore.online.repository.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ua.bookstore.online.utils.TestDataUtils.ID_1;
import static ua.bookstore.online.utils.TestDataUtils.beforeEachOrderTest;
import static ua.bookstore.online.utils.TestDataUtils.getClearedStatistics;
import static ua.bookstore.online.utils.TestDataUtils.getFirstOrder;
import static ua.bookstore.online.utils.TestDataUtils.getUser;
import static ua.bookstore.online.utils.TestDataUtils.tearDown;
import static ua.bookstore.online.utils.TestDataUtils.verifyCountOfDbCalls;

import jakarta.persistence.EntityManager;
import java.util.List;
import javax.sql.DataSource;
import org.hibernate.stat.Statistics;
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
    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    public void beforeEach(@Autowired DataSource dataSource) {
        beforeEachOrderTest(dataSource);
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        tearDown(dataSource);
    }

    @Test
    @DisplayName("Find all user's orders, returns list of orders")
    void findByUser_FindingAllUsersOrders_ReturnsListOfUsersOrders() {
        // Given
        Statistics statistics = getClearedStatistics(entityManager);
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

        verifyCountOfDbCalls(1, statistics);

    }

    @Test
    @DisplayName("Update order status by order ID, return count of modified rows")
    void updateStatusById_UpdateOrderStatusById_ReturnsOptionalOfUpdatedOrder() {
        // When
        int actual = orderRepository.updateStatusById(ID_1, Order.Status.PROCESSED);

        // Then
        assertEquals(1, actual, "Should be 1 modified row");
    }
}
