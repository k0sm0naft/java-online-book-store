package ua.bookstore.online.repository.shopping.cart.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ua.bookstore.online.utils.ConstantAndMethod.ID_1;
import static ua.bookstore.online.utils.ConstantAndMethod.NON_EXISTING_ID;
import static ua.bookstore.online.utils.ConstantAndMethod.beforeEachShoppingCartTest;
import static ua.bookstore.online.utils.ConstantAndMethod.getOrwellBook;
import static ua.bookstore.online.utils.ConstantAndMethod.getShoppingCart;
import static ua.bookstore.online.utils.ConstantAndMethod.tearDown;

import java.util.Optional;
import javax.sql.DataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ua.bookstore.online.model.Book;
import ua.bookstore.online.model.CartItem;
import ua.bookstore.online.model.ShoppingCart;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CartItemRepositoryTest {
    @Autowired
    private CartItemRepository cartItemRepository;

    @BeforeEach
    public void beforeEach(@Autowired DataSource dataSource) {
        beforeEachShoppingCartTest(dataSource);
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        tearDown(dataSource);
    }

    @Test
    @DisplayName("Find cart item by book and shopping cart")
    void findByBookAndShoppingCart_FindingCartItem_ReturnsOptionalOfCartItem() {
        // Given
        Book existingBook = getOrwellBook();
        Book nonExistingBook = new Book();
        nonExistingBook.setId(NON_EXISTING_ID);
        ShoppingCart shoppingCart = getShoppingCart();

        // When
        Optional<CartItem> actualExisted =
                cartItemRepository.findByBookAndShoppingCart(existingBook, shoppingCart);
        Optional<CartItem> actualEmpty =
                cartItemRepository.findByBookAndShoppingCart(nonExistingBook, shoppingCart);

        // Then
        assertTrue(actualEmpty.isEmpty(),
                "Optional of cart item by non-existing book should be empty");
        assertTrue(actualExisted.isPresent(),
                "Optional of cart item by existing book should be present");
    }

    @Test
    @DisplayName("Find if cart item exist by ID and shopping cart")
    void existsByIdAndShoppingCart_FindingCartItems_ReturnsOptionalOfCartItems() {
        // Given
        ShoppingCart shoppingCart = getShoppingCart();

        // When
        boolean actualExist =
                cartItemRepository.existsByIdAndShoppingCart(ID_1, shoppingCart);
        boolean actualNotExist =
                cartItemRepository.existsByIdAndShoppingCart(NON_EXISTING_ID, shoppingCart);

        // Then
        assertTrue(actualExist, "Optional of cart item by non-existing book should be empty");
        assertFalse(actualNotExist,
                "Optional of cart item by existing book should be present");
    }

    @Test
    @DisplayName("Remove cart item by ID and shopping cart")
    void deleteByIdAndShoppingCart_RemoveCartItem_SuccessfullyDeleted() {
        // Given
        ShoppingCart shoppingCart = getShoppingCart();
        int expectedLength = cartItemRepository.findAll().size() - 1;

        // When
        cartItemRepository.deleteByIdAndShoppingCart(ID_1, shoppingCart);

        // Then
        int actualLength = cartItemRepository.findAll().size();
        assertEquals(expectedLength, actualLength);
    }
}
