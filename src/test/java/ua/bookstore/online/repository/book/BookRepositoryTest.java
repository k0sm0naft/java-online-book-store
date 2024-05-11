package ua.bookstore.online.repository.book;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ua.bookstore.online.utils.ConstantAndMethod.ADD_CATEGORIES_FOR_BOOKS_SQL;
import static ua.bookstore.online.utils.ConstantAndMethod.ADD_CATEGORIES_SQL;
import static ua.bookstore.online.utils.ConstantAndMethod.ADD_THREE_BOOKS_SQL;
import static ua.bookstore.online.utils.ConstantAndMethod.ID;
import static ua.bookstore.online.utils.ConstantAndMethod.ISBN;
import static ua.bookstore.online.utils.ConstantAndMethod.NON_EXISTING_ID;
import static ua.bookstore.online.utils.ConstantAndMethod.NON_EXISTING_ISBN;
import static ua.bookstore.online.utils.ConstantAndMethod.TEAR_DOWN_DB_SQL;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.jdbc.Sql;
import ua.bookstore.online.model.Book;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookRepositoryTest {
    @Autowired
    private BookRepository bookRepository;

    @BeforeAll
    static void beforeAll(@Autowired DataSource dataSource) {
        tearDown(dataSource);
    }

    @SneakyThrows
    static void tearDown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(TEAR_DOWN_DB_SQL));
        }
    }

    @Test
    @DisplayName("Return optional of book by ISBN")
    @Sql(scripts = {
            ADD_THREE_BOOKS_SQL
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findByIsbn_FindingExistingAndNonExistingBookByIsbn_ReturnsOptionalOfBook() {
        // When
        Optional<Book> actualEmpty = bookRepository.findByIsbn(NON_EXISTING_ISBN);
        Optional<Book> actualExisted = bookRepository.findByIsbn(ISBN);

        // Then
        assertTrue(actualEmpty.isEmpty(), "Optional of book by non-existing ISBN should be empty");
        assertTrue(actualExisted.isPresent(),
                "Optional of book by existing ISBN should be present");
    }

    @Test
    @DisplayName("Return optional of book by ID with set of categories")
    @Sql(scripts = {
            ADD_THREE_BOOKS_SQL,
            ADD_CATEGORIES_SQL,
            ADD_CATEGORIES_FOR_BOOKS_SQL
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findByIdWithCategories_FindingExistingAndNonExistingBookById_ReturnsOptionalOfBook() {
        // When
        Optional<Book> actualEmpty = bookRepository.findByIdWithCategories(NON_EXISTING_ID);
        Optional<Book> actualExisted = bookRepository.findByIdWithCategories(ID);

        // Then
        assertTrue(actualEmpty.isEmpty(), "Optional of book by non-existing ID should be empty");
        assertTrue(actualExisted.isPresent(), "Optional of book by existing ID should be present");
        assertNotNull(actualExisted.get().getCategories());
    }

    @Test
    @DisplayName("Get all from DB with categories by params")
    @Sql(scripts = {
            ADD_THREE_BOOKS_SQL,
            ADD_CATEGORIES_SQL,
            ADD_CATEGORIES_FOR_BOOKS_SQL
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findAllByParams_GetAllBooks_ReturnsAllBooks() {
        // Given
        Specification<Book> specification = Specification.where(null);
        Pageable pageable = Pageable.unpaged();

        // When
        Page<Book> actual = bookRepository.findAll(specification, pageable);

        // Then
        int expectedSize = 3;
        assertEquals(expectedSize, actual.getSize());
        actual.stream()
              .map(Book::getCategories)
              .forEach(Assertions::assertNotNull);
    }

    @Test
    @DisplayName("Get all from DB with categories")
    @Sql(scripts = {
            ADD_THREE_BOOKS_SQL,
            ADD_CATEGORIES_SQL,
            ADD_CATEGORIES_FOR_BOOKS_SQL
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findAllBooks_GetAllBooks_ReturnsAllBooks() {
        // Given
        Pageable pageable = Pageable.unpaged();

        // When
        List<Book> actual = bookRepository.findAllBooks(pageable);

        // Then
        int expectedSize = 3;
        assertEquals(expectedSize, actual.size());
        actual.stream()
              .map(Book::getCategories)
              .forEach(Assertions::assertNotNull);
    }

    @Test
    @DisplayName("Get all books with belongs to category ID")
    @Sql(scripts = {
            ADD_THREE_BOOKS_SQL,
            ADD_CATEGORIES_SQL,
            ADD_CATEGORIES_FOR_BOOKS_SQL
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findAllByCategoryId_GetAllBooksByCategoryId_ReturnsAllBooksByCategoryId() {
        // Given
        Pageable pageable = Pageable.unpaged();
        Long classicId = 3L;

        // When
        List<Book> actualFiction = bookRepository.findAllByCategories_Id(ID, pageable);
        List<Book> actualClassic = bookRepository.findAllByCategories_Id(classicId, pageable);

        // Then
        assertNotNull(actualFiction);
        assertNotNull(actualClassic);
        int expectedSizeFiction = 2;
        int expectedSizeClassic = 1;
        assertEquals(expectedSizeFiction, actualFiction.size(), "Size should be equals");
        assertEquals(expectedSizeClassic, actualClassic.size(), "Size should be equals");
    }

    @Test
    @DisplayName("Get all books by ID and ISBN")
    @Sql(scripts = {
            ADD_THREE_BOOKS_SQL
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findAllByIdOrIsbn_GetAllBooksByIdAndIsbn_ReturnsAllBooksByIDAndIsbn() {
        // Given
        Long secondExistId = 2L;

        // When
        List<Book> actualTwo = bookRepository.findAllByIdOrIsbn(secondExistId, ISBN);
        List<Book> actualOne1 = bookRepository.findAllByIdOrIsbn(ID, ISBN);
        List<Book> actualOne2 = bookRepository.findAllByIdOrIsbn(NON_EXISTING_ID, ISBN);
        List<Book> actualOne3 = bookRepository.findAllByIdOrIsbn(ID, NON_EXISTING_ISBN);
        List<Book> actualZero =
                bookRepository.findAllByIdOrIsbn(NON_EXISTING_ID, NON_EXISTING_ISBN);

        // Then
        int expectedTwoSize = 2;
        int expectedOneSize = 1;
        int expectedZeroSize = 0;
        assertEquals(expectedTwoSize, actualTwo.size());
        assertEquals(expectedOneSize, actualOne1.size());
        assertEquals(expectedOneSize, actualOne2.size());
        assertEquals(expectedOneSize, actualOne3.size());
        assertEquals(expectedZeroSize, actualZero.size());
    }
}
