package ua.bookstore.online.repository.order.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ua.bookstore.online.utils.ConstantAndMethod.ID_1;
import static ua.bookstore.online.utils.ConstantAndMethod.ID_2;
import static ua.bookstore.online.utils.ConstantAndMethod.NON_EXISTING_ID;
import static ua.bookstore.online.utils.ConstantAndMethod.beforeEachOrderTest;
import static ua.bookstore.online.utils.ConstantAndMethod.getFirstOrder;
import static ua.bookstore.online.utils.ConstantAndMethod.getMalvilleOrderItem;
import static ua.bookstore.online.utils.ConstantAndMethod.getOrwellOrderItem;
import static ua.bookstore.online.utils.ConstantAndMethod.getUser;
import static ua.bookstore.online.utils.ConstantAndMethod.tearDown;

import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
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

    @BeforeEach
    public void beforeEach(@Autowired DataSource dataSource) {
        beforeEachOrderTest(dataSource);
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        tearDown(dataSource);
    }

    @Test
    @DisplayName("Get all order items from user's order by ID")
    void fidAllByOrderAndUser_GettingAllOrderItemsFromOrder_ReturnsListOfOrderItem() {
        // Given
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
        System.out.println(expextedBook);
        System.out.println(actualBook);
        assertTrue(EqualsBuilder.reflectionEquals(expextedBook, actualBook));

    }

    @Test
    @DisplayName("Find order item by ID and user's order ID")
    void findByIdAndOrder_FindingOrderItems_ReturnsOptionalOfOrderItems() {
        // Given
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
                .reflectionEquals(expected.getBook(), actualExisted.get().getBook()));
    }
}
