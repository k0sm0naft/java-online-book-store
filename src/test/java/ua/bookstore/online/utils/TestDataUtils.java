package ua.bookstore.online.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import ua.bookstore.online.dto.book.BookDto;
import ua.bookstore.online.dto.book.CreateBookRequestDto;
import ua.bookstore.online.dto.category.CategoryRequestDto;
import ua.bookstore.online.dto.category.CategoryResponseDto;
import ua.bookstore.online.dto.order.OrderItemResponseDto;
import ua.bookstore.online.dto.order.OrderResponseDto;
import ua.bookstore.online.dto.shopping.cart.CartItemRequestDto;
import ua.bookstore.online.dto.shopping.cart.CartItemResponseDto;
import ua.bookstore.online.dto.shopping.cart.QuantityDto;
import ua.bookstore.online.dto.shopping.cart.ShoppingCartDto;
import ua.bookstore.online.dto.user.UserRegistrationRequestDto;
import ua.bookstore.online.dto.user.UserResponseDto;
import ua.bookstore.online.model.Book;
import ua.bookstore.online.model.CartItem;
import ua.bookstore.online.model.Category;
import ua.bookstore.online.model.Order;
import ua.bookstore.online.model.OrderItem;
import ua.bookstore.online.model.Role;
import ua.bookstore.online.model.RoleName;
import ua.bookstore.online.model.ShoppingCart;
import ua.bookstore.online.model.User;

public final class TestDataUtils {
    public static final Long ID_1 = 1L;
    public static final Long ID_2 = 2L;
    public static final Long ID_3 = 3L;
    public static final Long NON_EXISTING_ID = 1234L;
    public static final int QUANTITY = 3;
    public static final String ISBN_ORWELL = "9780451524935";
    public static final String ISBN_MELVILLE = "9781503280781";
    public static final String NON_EXISTING_ISBN = "1234";
    public static final String AUTHOR_ORWELL = "George Orwell";
    public static final String AUTHOR_MELVILLE = "Herman Melville";
    public static final String TITLE_1984 = "1984";
    public static final String TITLE_MOBI_DICK = "Moby-Dick";
    public static final BigDecimal PRICE_ORWELL = BigDecimal.valueOf(12.99);
    public static final BigDecimal PRICE_MELVILLE = BigDecimal.valueOf(14.99);
    public static final BigDecimal TOTAL_RICE = BigDecimal.valueOf(68.95);
    public static final Set<Long> CATEGORY_IDS = Set.of(ID_1, ID_2);
    public static final String USER_EMAIL = "user@example.com";
    public static final String PASSWORD = "12345678";
    public static final String FIRST_NAME = "Username";
    public static final String LAST_NAME = "Userlastname";
    public static final LocalDateTime ORDER_DATE = LocalDateTime.of(2022, 5, 23, 8, 54);
    public static final String FICTION = "Fiction";
    public static final String FICTION_DESCRIPTION = "Fiction description";
    public static final String SHIPPING_ADDRESS = "shipping address";
    public static final String CLASSPATH = "classpath:";
    public static final String ADD_THREE_BOOKS_SQL = "database/books/add-three-books.sql";
    public static final String ADD_CATEGORIES_SQL = "database/categories/add-categories.sql";
    public static final String ADD_CATEGORIES_FOR_BOOKS_SQL =
            "database/books/add-categories-for-books.sql";
    public static final String TEAR_DOWN_DB_SQL = "database/tear-down-db.sql";
    public static final String ADD_CART_ITEMS_SQL =
            "database/shopping.carts/items/add-cart_items.sql";
    public static final String ADD_THREE_SHOPPING_CARTS_SQL =
            "database/shopping.carts/add-three-shopping_carts.sql";
    public static final String ADD_USERS_SQL = "database/users/add-users.sql";
    public static final String ADD_USERS_ROLES = "database/users/add-users_roles.sql";
    public static final String ADD_ROLES = "database/roles/add-roles.sql";
    public static final String ADD_THREE_ORDERS_SQL = "database/orders/add-three-orders.sql";
    public static final String ADD_ORDER_ITEMS_SQL = "database/orders/items/add-order_items.sql";

    private TestDataUtils() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }

    @SneakyThrows
    public static void tearDown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection, new ClassPathResource(TEAR_DOWN_DB_SQL));
        }
    }

    @SneakyThrows
    public static void beforeEachOrderTest(DataSource dataSource) {
        tearDown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(ADD_USERS_SQL));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(ADD_THREE_BOOKS_SQL));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(ADD_THREE_SHOPPING_CARTS_SQL));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(ADD_CART_ITEMS_SQL));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(ADD_THREE_ORDERS_SQL));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(ADD_ORDER_ITEMS_SQL));
        }
    }

    @SneakyThrows
    public static void beforeEachShoppingCartTest(DataSource dataSource) {
        tearDown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(ADD_USERS_SQL));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(ADD_THREE_SHOPPING_CARTS_SQL));
            ScriptUtils.executeSqlScript(connection, new ClassPathResource(ADD_THREE_BOOKS_SQL));
            ScriptUtils.executeSqlScript(connection, new ClassPathResource(ADD_CATEGORIES_SQL));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(ADD_CATEGORIES_FOR_BOOKS_SQL));
            ScriptUtils.executeSqlScript(connection, new ClassPathResource(ADD_CART_ITEMS_SQL));
        }
    }

    @SneakyThrows
    public static void beforeEachBookRepositoryTest(@Autowired DataSource dataSource) {
        tearDown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(ADD_THREE_BOOKS_SQL));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(ADD_CATEGORIES_SQL));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(ADD_CATEGORIES_FOR_BOOKS_SQL));
        }
    }

    public static Statistics getClearedStatistics(EntityManager entityManager) {
        SessionFactory sessionFactory = entityManager.getEntityManagerFactory()
                                                     .unwrap(SessionFactory.class);
        Statistics statistics = sessionFactory.getStatistics();
        statistics.setStatisticsEnabled(true);
        statistics.clear();
        return statistics;
    }

    public static void verifyCountOfDbCalls(int count, Statistics statistics) {
        long queryCount = statistics.getPrepareStatementCount();
        assertEquals(count, queryCount, count + " query should be executed");
    }

    public static CreateBookRequestDto createBookRequestDto() {
        return getRequestDto(TITLE_1984);
    }

    public static CreateBookRequestDto getRequestDto(String title) {
        return CreateBookRequestDto.builder()
                                   .title(title)
                                   .author(AUTHOR_ORWELL)
                                   .isbn(ISBN_ORWELL)
                                   .categoryIds(CATEGORY_IDS)
                                   .price(PRICE_ORWELL)
                                   .build();
    }

    public static Book createBook() {
        Book book = new Book();
        book.setIsbn(ISBN_ORWELL);
        book.setAuthor(AUTHOR_ORWELL);
        book.setTitle(TITLE_1984);
        book.setPrice(PRICE_ORWELL);
        book.setCategories(getCategories());
        return book;
    }

    public static BookDto getBookDto(Book book) {
        return BookDto.builder()
                      .categoryIds(
                              book.getCategories().stream()
                                  .map(Category::getId)
                                  .collect(Collectors.toSet()))
                      .title(book.getTitle())
                      .author(book.getAuthor())
                      .isbn(book.getIsbn())
                      .price(book.getPrice())
                      .build();
    }

    public static BookDto getOrwell() {
        return getNewOrwell(TITLE_1984);
    }

    public static BookDto getNewOrwell(String title) {
        return BookDto.builder()
                      .id(ID_1)
                      .title(title)
                      .author(AUTHOR_ORWELL)
                      .isbn(ISBN_ORWELL)
                      .categoryIds(CATEGORY_IDS)
                      .price(PRICE_ORWELL)
                      .build();
    }

    public static BookDto getMelville() {
        return BookDto.builder()
                      .id(ID_2)
                      .title(TITLE_MOBI_DICK)
                      .author(AUTHOR_MELVILLE)
                      .categoryIds(Set.of(ID_1, ID_3))
                      .isbn(ISBN_MELVILLE)
                      .price(PRICE_MELVILLE)
                      .build();
    }

    public static Set<Category> getCategories() {
        return CATEGORY_IDS.stream()
                           .map(Category::new)
                           .collect(Collectors.toSet());
    }

    public static CategoryRequestDto getCategoryRequest() {
        return new CategoryRequestDto(FICTION, FICTION_DESCRIPTION);
    }

    public static Category createCategory() {
        Category category = new Category();
        category.setName(FICTION);
        category.setDescription(FICTION_DESCRIPTION);
        return category;
    }

    public static CategoryResponseDto getFiction() {
        return CategoryResponseDto.builder()
                                  .id(ID_1)
                                  .name(FICTION)
                                  .description(FICTION_DESCRIPTION)
                                  .build();
    }

    public static CategoryResponseDto getDystopian() {
        return CategoryResponseDto.builder()
                                  .id(ID_2)
                                  .name("Dystopian")
                                  .description("Dystopian description")
                                  .build();
    }

    public static CategoryResponseDto getClassic() {
        return CategoryResponseDto.builder()
                                  .id(ID_3)
                                  .name("Classic")
                                  .description("Classic description")
                                  .build();
    }

    public static User getUser() {
        User user = new User();
        user.setId(ID_1);
        user.setEmail(USER_EMAIL);
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setPassword(PASSWORD);
        user.setShippingAddress(SHIPPING_ADDRESS);
        user.setRoles(Set.of(getUserRole()));
        return user;
    }

    public static Role getUserRole() {
        Role role = new Role();
        role.setName(RoleName.ROLE_USER);
        return role;
    }

    public static ShoppingCart getShoppingCart() {
        ShoppingCart shoppingCart = new ShoppingCart(getUser());
        shoppingCart.setId(ID_1);
        return shoppingCart;
    }

    public static Book getOrwellBook() {
        Book book = new Book();
        book.setId(ID_1);
        book.setTitle(TITLE_1984);
        book.setAuthor(AUTHOR_ORWELL);
        book.setIsbn(ISBN_ORWELL);
        book.setPrice(PRICE_ORWELL);
        return book;
    }

    public static Book getMalvillBook() {
        Book book = new Book();
        book.setId(ID_2);
        book.setTitle(TITLE_MOBI_DICK);
        book.setAuthor(AUTHOR_MELVILLE);
        book.setIsbn(ISBN_MELVILLE);
        book.setPrice(PRICE_MELVILLE);
        return book;
    }

    public static ShoppingCartDto getEmptyShoppingCartDto() {
        return getShoppingCartDto(List.of());
    }

    public static ShoppingCartDto getShoppingCartDto(
            List<CartItemResponseDto> cartItemResponseDtos) {
        return new ShoppingCartDto(ID_1, ID_1, cartItemResponseDtos);
    }

    public static CartItemResponseDto getCartItemResponseDto() {
        return new CartItemResponseDto(ID_1, ID_1, TITLE_1984, QUANTITY);
    }

    public static QuantityDto getQuantityDto() {
        return new QuantityDto(QUANTITY);
    }

    public static CartItemRequestDto getCartItemRequestDto() {
        return new CartItemRequestDto(ID_1, QUANTITY);
    }

    public static CartItem getCartItem() {
        CartItem cartItem = new CartItem();
        cartItem.setId(ID_1);
        cartItem.setBook(getOrwellBook());
        cartItem.setQuantity(QUANTITY);
        return cartItem;
    }

    public static Order getFirstOrder() {
        Order firstOrder = new Order();
        firstOrder.setId(ID_1);
        firstOrder.setUser(getUser());
        firstOrder.setShippingAddress(SHIPPING_ADDRESS);
        firstOrder.setOrderItems(Set.of(getOrwellOrderItem(ID_1, firstOrder),
                getMalvilleOrderItem(ID_2, firstOrder)));
        return firstOrder;
    }

    public static Order getSecondOrder() {
        Order secondOrder = new Order();
        secondOrder.setId(ID_2);
        secondOrder.setUser(getUser());
        secondOrder.setShippingAddress(SHIPPING_ADDRESS);
        secondOrder.setOrderItems(Set.of(getMalvilleOrderItem(ID_3, secondOrder)));
        return secondOrder;
    }

    public static OrderItem getOrwellOrderItem(Long orderItemId, Order order) {
        OrderItem orwellOrderItem = new OrderItem();
        orwellOrderItem.setOrder(order);
        orwellOrderItem.setBook(getOrwellBook());
        orwellOrderItem.setPrice(PRICE_ORWELL);
        orwellOrderItem.setQuantity(QUANTITY);
        orwellOrderItem.setId(orderItemId);
        return orwellOrderItem;
    }

    public static OrderItem getMalvilleOrderItem(Long itemID, Order order) {
        OrderItem malvilleOrderItem = new OrderItem();
        malvilleOrderItem.setOrder(order);
        malvilleOrderItem.setBook(getMalvillBook());
        malvilleOrderItem.setPrice(PRICE_MELVILLE);
        malvilleOrderItem.setQuantity(QUANTITY);
        malvilleOrderItem.setId(itemID);
        return malvilleOrderItem;
    }

    public static OrderResponseDto getFirstOrderResponseDto() {
        return new OrderResponseDto(ID_1, ID_1,
                List.of(getOrwellOrderItemResponse(), getMalvilleOrderItemResponse()),
                ORDER_DATE, TOTAL_RICE,
                "PENDING");
    }

    public static OrderResponseDto getSecondOrderResponseDto() {
        return new OrderResponseDto(ID_2, ID_1, List.of(getMalvilleOrderItemResponse()),
                null, TOTAL_RICE, "DELIVERED");
    }

    public static OrderItemResponseDto getOrwellOrderItemResponse() {
        return new OrderItemResponseDto(ID_1, ID_1, QUANTITY);
    }

    public static OrderItemResponseDto getMalvilleOrderItemResponse() {
        return new OrderItemResponseDto(ID_2, ID_2, 2);
    }

    public static UserRegistrationRequestDto getUserRequestDto() {
        return new UserRegistrationRequestDto(USER_EMAIL, PASSWORD, PASSWORD, FIRST_NAME,
                LAST_NAME, SHIPPING_ADDRESS);
    }

    public static UserResponseDto getUserResponseDto() {
        return new UserResponseDto(ID_1, USER_EMAIL, FIRST_NAME, LAST_NAME, SHIPPING_ADDRESS);
    }
}
