package ua.bookstore.online.utils;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Set;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import ua.bookstore.online.dto.book.BookDto;
import ua.bookstore.online.dto.book.CreateBookRequestDto;
import ua.bookstore.online.dto.category.CategoryRequestDto;
import ua.bookstore.online.dto.category.CategoryResponseDto;
import ua.bookstore.online.model.Book;
import ua.bookstore.online.model.Category;
import ua.bookstore.online.model.ShoppingCart;
import ua.bookstore.online.model.User;

public class ConstantAndMethod {
    public static final Long ID_1 = 1L;
    public static final String NON_EXISTING_ISBN = "1234";
    public static final String ISBN = "9780451524935";
    public static final Long NON_EXISTING_ID = 1234L;
    public static final String AUTHOR = "George Orwell";
    public static final String TITLE = "1984";
    public static final BigDecimal PRICE = BigDecimal.valueOf(12.99);
    public static final long ID_2 = 2L;
    public static final Set<Long> CATEGORY_IDS = Set.of(ID_1, ID_2);
    public static final String CLASSPATH = "classpath:";
    public static final String ADD_THREE_BOOKS_SQL = "database/books/add-three-books.sql";
    public static final String ADD_CATEGORIES_SQL = "database/books/add-categories.sql";
    public static final String ADD_CATEGORIES_FOR_BOOKS_SQL =
            "database/books/add-categories-for-books.sql";
    public static final String ADD_THREE_CATEGORIES_SQL =
            "database/categories/add-three-categories.sql";
    public static final String CLASSPATH_ADD_THREE_CATEGORIES_SQL =
            CLASSPATH + ADD_THREE_CATEGORIES_SQL;
    public static final String TEAR_DOWN_DB_SQL = "database/tear-down-db.sql";
    public static final String ADD_CART_ITEMS_SQL =
            "database/shopping.carts/items/add-cart_items.sql";
    public static final String ADD_THREE_SHOPPING_CARTS_SQL =
            "database/shopping.carts/add-three-shopping_carts.sql";
    public static final String ADD_USERS_FOR_SHOPPING_CARTS_SQL =
            "database/shopping.carts/add-users-for-shopping_carts.sql";
    public static final String FICTION = "Fiction";
    public static final String FICTION_DESCRIPTION = "Fiction description";
    public static final long ID_3 = 3L;

    @SneakyThrows
    public static void tearDown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(TEAR_DOWN_DB_SQL));
        }
    }

    @SneakyThrows
    public static void beforeEachShoppingCartRepositoryTest(@Autowired DataSource dataSource) {
        tearDown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(ADD_USERS_FOR_SHOPPING_CARTS_SQL));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(ADD_THREE_SHOPPING_CARTS_SQL));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(ADD_THREE_BOOKS_SQL));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(ADD_CART_ITEMS_SQL));
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

    public static CreateBookRequestDto createBookRequestDto() {
        return getRequestDto(TITLE);
    }

    public static CreateBookRequestDto getRequestDto(String title) {
        return CreateBookRequestDto.builder()
                                   .title(title)
                                   .author(AUTHOR)
                                   .isbn(ISBN)
                                   .categoryIds(CATEGORY_IDS)
                                   .price(PRICE)
                                   .build();
    }

    public static Book createBook() {
        Book book = new Book();
        book.setIsbn(ISBN);
        book.setAuthor(AUTHOR);
        book.setTitle(TITLE);
        book.setPrice(PRICE);
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
        return getNewOrwell(TITLE);
    }

    public static BookDto getNewOrwell(String title) {
        return BookDto.builder()
                      .id(ID_1)
                      .title(title)
                      .author(AUTHOR)
                      .isbn(ISBN)
                      .categoryIds(CATEGORY_IDS)
                      .price(PRICE)
                      .build();
    }

    public static BookDto getMelville() {
        return BookDto.builder()
                      .id(ID_2)
                      .title("Moby-Dick")
                      .author("Herman Melville")
                      .categoryIds(Set.of(ID_1, ID_3))
                      .isbn("9781503280781")
                      .price(BigDecimal.valueOf(14.99))
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

    public static CategoryResponseDto getAdventure() {
        return CategoryResponseDto.builder()
                                  .id(ID_2)
                                  .name("Adventure")
                                  .description("Adventure description")
                                  .build();
    }

    public static CategoryResponseDto getClassic() {
        return CategoryResponseDto.builder()
                                  .id(ID_3)
                                  .name("Classic")
                                  .description("Classic description")
                                  .build();
    }

    public static User getUser(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    public static ShoppingCart getShoppingCart() {
        ShoppingCart shoppingCart = new ShoppingCart(getUser(ID_1));
        shoppingCart.setId(ID_1);
        return shoppingCart;
    }

    public static Book getBook(Long id) {
        Book book = new Book();
        book.setId(id);
        return book;
    }
}
