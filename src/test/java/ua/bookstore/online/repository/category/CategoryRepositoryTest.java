package ua.bookstore.online.repository.category;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.jdbc.Sql;
import ua.bookstore.online.model.Category;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CategoryRepositoryTest {
    private static final long ID_1 = 1L;
    private static final long ID_2 = 2L;
    private static final long ID_3 = 3L;
    private static final long ID_4 = 4L;
    private static final long ID_5 = 5L;
    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeAll
    static void beforeAll(@Autowired DataSource dataSource) {
        tearDown(dataSource);
    }

    @SneakyThrows
    static void tearDown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/tear-down-db.sql"));
        }
    }

    @Test
    @DisplayName("Find all categories by IDs")
    @Sql(scripts = {
            "classpath:database/categories/add-three-categories.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findAllByIdIn_ReturnsCategoriesForGivenIds() {
        // Given
        Set<Long> categoryIds = Set.of(ID_1, ID_2);
        List<Long> expected = List.of(ID_1, ID_2);

        // When
        Set<Category> actual = categoryRepository.findAllByIdIn(categoryIds);

        // Then
        assertEquals(expected.size(), actual.size());
        assertTrue(actual.stream()
                         .map(Category::getId)
                         .collect(Collectors.toSet())
                         .containsAll(expected));
    }

    @Test
    @DisplayName("Find all categories by IDs when some IDs are invalid")
    @Sql(scripts = {
            "classpath:database/categories/add-three-categories.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findAllByIdIn_WithInvalidIds_ReturnsValidCategories() {
        // Given
        Set<Long> categoryIds = Set.of(ID_1, ID_2, ID_3, ID_4, ID_5);
        List<Long> expected = List.of(ID_1, ID_2, ID_3);

        // When
        Set<Category> actual = categoryRepository.findAllByIdIn(categoryIds);

        // Then
        assertEquals(expected.size(), actual.size());
        assertTrue(actual.stream()
                         .map(Category::getId)
                         .collect(Collectors.toSet())
                         .containsAll(expected));
    }
}
