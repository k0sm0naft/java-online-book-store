package ua.bookstore.online.repository.shopping.cart;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ua.bookstore.online.utils.TestDataUtils.NON_EXISTING_ID;
import static ua.bookstore.online.utils.TestDataUtils.beforeEachShoppingCartTest;
import static ua.bookstore.online.utils.TestDataUtils.getClearedStatistics;
import static ua.bookstore.online.utils.TestDataUtils.getUser;
import static ua.bookstore.online.utils.TestDataUtils.tearDown;
import static ua.bookstore.online.utils.TestDataUtils.verifyCountOfDbCalls;

import jakarta.persistence.EntityManager;
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
import ua.bookstore.online.model.ShoppingCart;
import ua.bookstore.online.model.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ShoppingCartRepositoryTest {
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;
    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    public void beforeEach(@Autowired DataSource dataSource) {
        beforeEachShoppingCartTest(dataSource);
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        tearDown(dataSource);
    }

    @Test
    @DisplayName("Find shopping cart by user, returns optional of shopping cart")
    void findByUser_FindingExistingAndNonExistingShoppingCarts_ReturnsOptionalOfShoppingCarts() {
        // Given
        User existingUser = getUser();
        User nonExistingUser = new User();
        nonExistingUser.setId(NON_EXISTING_ID);

        // When
        Optional<ShoppingCart> actualEmpty = shoppingCartRepository.findByUser(nonExistingUser);
        Optional<ShoppingCart> actualExisted = shoppingCartRepository.findByUser(existingUser);

        // Then
        assertTrue(actualEmpty.isEmpty(),
                "Optional of shopping cart by non-existing user should be empty");
        assertTrue(actualExisted.isPresent(),
                "Optional of shopping cart by existing user should be present");
    }

    @Test
    @DisplayName("Find shopping cart by user with cart items, returns optional of shopping cart with cart items")
    void findByUserWithCartItems_FindingExistingShoppingCart_ReturnsOptionalOfCartWithCartItems() {
        // Given
        Statistics statistics = getClearedStatistics(entityManager);
        User user = getUser();

        // When
        Optional<ShoppingCart> actual = shoppingCartRepository.findByUserWithCartItems(user);

        // Then
        assertTrue(actual.isPresent(),
                "Optional of shopping cart by existing user should be present");
        int sizeOfCartItems = 2;
        assertEquals(sizeOfCartItems, actual.get().getCartItems().size());

        verifyCountOfDbCalls(1, statistics);
    }
}
