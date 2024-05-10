package ua.bookstore.online.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;
import ua.bookstore.online.dto.book.BookDto;
import ua.bookstore.online.dto.category.CategoryRequestDto;
import ua.bookstore.online.dto.category.CategoryResponseDto;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CategoryControllerIntegrationTest {
    private static final Long EXISTING_ID = 1L;
    private static final String URI = "/categories";
    private static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired DataSource dataSource,
            @Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
    }

    @AfterEach
    void afterEach(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @SneakyThrows
    static void teardown(@Autowired DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/tear-down-db.sql"));
        }
    }

    @Test
    @DisplayName("Create new category")
    @WithMockUser(username = "admin", roles = {"MANAGER"})
    void createCategory_CreateNewCategory_ReturnsExpectedCategory() throws Exception {
        // Given
        CategoryRequestDto requestDto = CategoryRequestDto.builder()
                                                          .name("Classic")
                                                          .description("Classic description")
                                                          .build();
        CategoryResponseDto expected = getClassic();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        MvcResult result = mockMvc.perform(post(URI)
                                          .content(jsonRequest)
                                          .contentType(MediaType.APPLICATION_JSON))
                                  .andExpect(status().isCreated())
                                  .andReturn();

        // Then
        CategoryResponseDto actual =
                objectMapper.readValue(result.getResponse().getContentAsString(),
                        CategoryResponseDto.class);
        assertNotNull(actual);
        assertNotNull(actual.id());
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual, "id"));
    }

    @Test
    @DisplayName("Update existing category")
    @WithMockUser(username = "admin", roles = {"MANAGER"})
    @Sql(scripts = {
            "classpath:database/categories/add-three-categories.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void updateCategory_UpdateExistingCategory_ReturnsExpectedCategory() throws Exception {
        // Given
        CategoryRequestDto requestDto = CategoryRequestDto.builder()
                                                          .name("updatedName")
                                                          .description("updatedDescription")
                                                          .build();
        CategoryResponseDto expected = CategoryResponseDto.builder()
                                                          .id(EXISTING_ID)
                                                          .name("updatedName")
                                                          .description("updatedDescription")
                                                          .build();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        String url = URI + "/" + EXISTING_ID;

        // When
        MvcResult result = mockMvc.perform(put(url)
                                          .content(jsonRequest)
                                          .contentType(MediaType.APPLICATION_JSON))
                                  .andExpect(status().isAccepted())
                                  .andReturn();

        // Then
        CategoryResponseDto actual =
                objectMapper.readValue(result.getResponse().getContentAsString(),
                        CategoryResponseDto.class);
        assertNotNull(actual);
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
    }

    @Test
    @DisplayName("Get two existing category")
    @WithMockUser
    @Sql(scripts = {
            "classpath:database/categories/add-three-categories.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getAll_GetTwoExistingCategories_ReturnsExpectedCategories() throws Exception {
        // Given
        List<CategoryResponseDto> expected = List.of(getFiction(), getAdventure());

        // When
        MvcResult result = mockMvc.perform(get(URI)
                                          .param("page", "0")
                                          .param("size", "2")
                                          .param("sort", "id")
                                          .contentType(MediaType.APPLICATION_JSON))
                                  .andExpect(status().isOk())
                                  .andReturn();

        // Then
        CategoryResponseDto[] actual =
                objectMapper.readValue(result.getResponse().getContentAsString(),
                        CategoryResponseDto[].class);
        assertNotNull(actual);
        assertEquals(expected.size(), actual.length);
        assertEquals(expected, Arrays.stream(actual).toList());
    }

    @Test
    @DisplayName("Get existing category by ID")
    @WithMockUser
    @Sql(scripts = {
            "classpath:database/categories/add-three-categories.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getCategoryById_GetExistingCategory_ReturnsExpectedCategory() throws Exception {
        // Given
        String url = URI + "/" + EXISTING_ID;
        CategoryResponseDto expected = getFiction();

        // When
        MvcResult result = mockMvc.perform(get(url)
                                          .contentType(MediaType.APPLICATION_JSON))
                                  .andExpect(status().isOk())
                                  .andReturn();

        // Then
        CategoryResponseDto actual =
                objectMapper.readValue(result.getResponse().getContentAsString(),
                        CategoryResponseDto.class);
        assertNotNull(actual);
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
    }

    @Test
    @DisplayName("Get all books from existing category by ID")
    @WithMockUser
    @Sql(scripts = {
            "classpath:database/books/add-categories.sql",
            "classpath:database/books/add-three-books.sql",
            "classpath:database/books/add-categories-for-books.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getBooksByCategoryId_GetAllBooksByCategory_ReturnsExpectedBooks() throws Exception {
        // Given
        List<BookDto> expected = List.of(
                BookDto.builder().id(1L).title("1984").author("George Orwell")
                       .isbn("9780451524935").price(BigDecimal.valueOf(12.99)).build(),
                BookDto.builder().id(2L).title("Moby-Dick").author("Herman Melville")
                       .isbn("9781503280781").price(BigDecimal.valueOf(14.99)).build());
        String url = URI + "/" + EXISTING_ID + "/books";

        // When
        MvcResult result = mockMvc.perform(get(url)
                                          .contentType(MediaType.APPLICATION_JSON))
                                  .andExpect(status().isOk())
                                  .andReturn();

        // Then
        BookDto[] actual =
                objectMapper.readValue(result.getResponse().getContentAsString(), BookDto[].class);
        assertNotNull(actual);
        assertEquals(expected.size(), actual.length);
        assertEquals(expected, Arrays.stream(actual).toList());
    }

    @Test
    @DisplayName("Delete existing category")
    @WithMockUser(username = "admin", roles = {"MANAGER"})
    @Sql(scripts = {
            "classpath:database/categories/add-three-categories.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void deleteCategory_DeleteExistingCategory_SuccessfullyDeleted() throws Exception {
        // Given
        String url = URI + "/" + EXISTING_ID;

        // When
        mockMvc.perform(delete(url)
                       .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();

    }

    private static CategoryResponseDto getFiction() {
        return CategoryResponseDto.builder()
                                  .id(1L)
                                  .name("Fiction")
                                  .description("Fiction description")
                                  .build();
    }

    private static CategoryResponseDto getAdventure() {
        return CategoryResponseDto.builder()
                                  .id(2L)
                                  .name("Adventure")
                                  .description("Adventure description")
                                  .build();
    }

    private static CategoryResponseDto getClassic() {
        return CategoryResponseDto.builder()
                                  .id(3L)
                                  .name("Classic")
                                  .description("Classic description")
                                  .build();
    }
}
