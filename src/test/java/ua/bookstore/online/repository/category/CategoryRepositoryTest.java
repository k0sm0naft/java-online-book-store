package ua.bookstore.online.repository.category;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ua.bookstore.online.utils.TestDataUtils.ADD_CATEGORIES_SQL;
import static ua.bookstore.online.utils.TestDataUtils.CLASSPATH;
import static ua.bookstore.online.utils.TestDataUtils.ID_1;
import static ua.bookstore.online.utils.TestDataUtils.ID_2;
import static ua.bookstore.online.utils.TestDataUtils.ID_3;
import static ua.bookstore.online.utils.TestDataUtils.tearDown;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import ua.bookstore.online.model.Category;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CategoryRepositoryTest {
    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeAll
    static void beforeAll(@Autowired DataSource dataSource) {
        tearDown(dataSource);
    }

    @Test
    @DisplayName("Find all categories by IDs, returns set of categories")
    @Sql(scripts = {
            CLASSPATH + ADD_CATEGORIES_SQL
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findAllByIdIn_FindAllCategoriesById_ReturnsCategoriesForGivenIds() {
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
    @DisplayName("Find all categories by IDs when some IDs are invalid, return list of existing IDs")
    @Sql(scripts = {
            CLASSPATH + ADD_CATEGORIES_SQL
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findAllByIdIn_FindAllCategoriesWithSomeInvalidIds_ReturnsValidCategories() {
        // Given
        Set<Long> categoryIds = Set.of(ID_1, ID_2, ID_3, 6L, 7L);
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
