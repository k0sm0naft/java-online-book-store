package ua.bookstore.online.repository.order.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ua.bookstore.online.utils.TestDataUtils.ID_1;
import static ua.bookstore.online.utils.TestDataUtils.ID_2;
import static ua.bookstore.online.utils.TestDataUtils.NON_EXISTING_ID;
import static ua.bookstore.online.utils.TestDataUtils.beforeEachOrderTest;
import static ua.bookstore.online.utils.TestDataUtils.getClearedStatistics;
import static ua.bookstore.online.utils.TestDataUtils.getFirstOrder;
import static ua.bookstore.online.utils.TestDataUtils.getMalvilleOrderItem;
import static ua.bookstore.online.utils.TestDataUtils.getOrwellOrderItem;
import static ua.bookstore.online.utils.TestDataUtils.getUser;
import static ua.bookstore.online.utils.TestDataUtils.tearDown;
import static ua.bookstore.online.utils.TestDataUtils.verifyCountOfDbCalls;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
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
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;
import ua.bookstore.online.model.Book;
import ua.bookstore.online.model.OrderItem;
import ua.bookstore.online.model.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OrderItemRepositoryTest {
    @Autowired
    private OrderItemRepository orderItemRepository;
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
    @DisplayName("Find all order items from user's order by ID, returns list of order items")
    void fidAllByOrderAndUser_GettingAllOrderItemsFromOrder_ReturnsListOfOrderItem() {
        // Given
        Statistics statistics = getClearedStatistics(entityManager);
        List<OrderItem> expected = List.of(getOrwellOrderItem(ID_1, getFirstOrder()),
                getMalvilleOrderItem(ID_2, getFirstOrder()));

        // When
        List<OrderItem> actual =
                orderItemRepository
                        .findAllByOrder_IdAndOrder_User(ID_1, getUser(), Pageable.unpaged());

        // Then
        assertEquals(expected.size(), actual.size());
        Book expextedBook = expected.getFirst().getBook();
        Book actualBook = actual.getFirst().getBook();
        assertTrue(EqualsBuilder.reflectionEquals(expextedBook, actualBook, "categories"));

        verifyCountOfDbCalls(1, statistics);
    }

    @Test
    @DisplayName("Find order item by ID and user's order ID, returns optional of order item")
    void findByIdAndOrder_FindingOrderItems_ReturnsOptionalOfOrderItems() {
        // Given
        Statistics statistics = getClearedStatistics(entityManager);
        User user = getUser();
        OrderItem expected = getOrwellOrderItem(ID_1, getFirstOrder());

        // When
        Optional<OrderItem> actualExisted =
                orderItemRepository.findByIdAndOrder_IdAndOrder_User(ID_1, ID_1, user);
        Optional<OrderItem> actualNotExisted =
                orderItemRepository.findByIdAndOrder_IdAndOrder_User(NON_EXISTING_ID, ID_1, user);

        // Then
        assertTrue(actualExisted.isPresent(),
                "Optional of non-existing order item should be present");
        assertTrue(actualNotExisted.isEmpty(), "Optional of existing order item should be empty");
        assertTrue(EqualsBuilder.reflectionEquals(expected, actualExisted.get(), "order", "book"));
        assertTrue(EqualsBuilder
                .reflectionEquals(expected.getBook(), actualExisted.get().getBook(), "categories"));

        verifyCountOfDbCalls(2, statistics);
    }
}
