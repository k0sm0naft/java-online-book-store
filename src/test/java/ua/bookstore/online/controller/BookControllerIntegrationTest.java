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
import java.util.Set;
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
import ua.bookstore.online.dto.book.CreateBookRequestDto;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookControllerIntegrationTest {
    private static final double PRICE = 12.99;
    private static final Long EXISTING_ID = 1L;
    private static final String URI = "/books";
    private static final String EXISTING_ISBN = "9780451524935";
    private static final String EXISTING_AUTHOR = "George Orwell";
    private static final String EXISTING_TITLE = "1984";
    private static final Set<Long> EXISTING_CATEGORY_IDS = Set.of(1L, 2L);
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
    @DisplayName("Create new book")
    @WithMockUser(username = "admin", roles = {"MANAGER"})
    @Sql(scripts = {
            "classpath:database/books/add-categories.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void createBook_CreateNewBook_ReturnsExpectedBook() throws Exception {
        // Given
        CreateBookRequestDto requestDto = getRequestDto(EXISTING_TITLE);
        BookDto expected = getOrwell(EXISTING_TITLE);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        MvcResult result = mockMvc.perform(post(URI)
                                          .content(jsonRequest)
                                          .contentType(MediaType.APPLICATION_JSON))
                                  .andExpect(status().isCreated())
                                  .andReturn();

        // Then
        BookDto actual =
                objectMapper.readValue(result.getResponse().getContentAsString(),
                        BookDto.class);
        assertNotNull(actual);
        assertNotNull(actual.id());
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual, "id"));
    }

    @Test
    @DisplayName("Update existing book")
    @WithMockUser(username = "admin", roles = {"MANAGER"})
    @Sql(scripts = {
            "classpath:database/books/add-categories.sql",
            "classpath:database/books/add-three-books.sql",
            "classpath:database/books/add-categories-for-books.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void updateBook_UpdateExisingBook_ReturnsExpectedBook() throws Exception {
        // Given
        String updatedTitle = "updatedTitle";
        CreateBookRequestDto requestDto = getRequestDto(updatedTitle);
        BookDto expected = getOrwell(updatedTitle);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        String url = URI + "/" + EXISTING_ID;

        // When
        MvcResult result = mockMvc.perform(put(url)
                                          .content(jsonRequest)
                                          .contentType(MediaType.APPLICATION_JSON))
                                  .andExpect(status().isAccepted())
                                  .andReturn();

        // Then
        BookDto actual =
                objectMapper.readValue(result.getResponse().getContentAsString(),
                        BookDto.class);
        assertNotNull(actual);
        assertNotNull(actual.id());
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
    }

    @Test
    @DisplayName("Get existing book by ID")
    @WithMockUser
    @Sql(scripts = {
            "classpath:database/books/add-categories.sql",
            "classpath:database/books/add-three-books.sql",
            "classpath:database/books/add-categories-for-books.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getBookById_GetExistingBook_ReturnsExpectedBook() throws Exception {
        // Given
        BookDto expected = getOrwell(EXISTING_TITLE);
        String url = URI + "/" + EXISTING_ID;

        // When
        MvcResult result = mockMvc.perform(get(url)
                                          .contentType(MediaType.APPLICATION_JSON))
                                  .andExpect(status().isOk())
                                  .andReturn();

        // Then
        BookDto actual =
                objectMapper.readValue(result.getResponse().getContentAsString(),
                        BookDto.class);
        assertNotNull(actual);
        assertNotNull(actual.id());
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
    }

    @Test
    @DisplayName("Get two existing books")
    @WithMockUser
    @Sql(scripts = {
            "classpath:database/books/add-categories.sql",
            "classpath:database/books/add-three-books.sql",
            "classpath:database/books/add-categories-for-books.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getAll_GetTwoExistingBooks_ReturnsExpectedBooks() throws Exception {
        // Given
        List<BookDto> expected = List.of(getOrwell(EXISTING_TITLE), getMelville());

        // When
        MvcResult result = mockMvc.perform(get(URI)
                                          .param("page", "0")
                                          .param("size", "2")
                                          .param("sort", "id")
                                          .contentType(MediaType.APPLICATION_JSON))
                                  .andExpect(status().isOk())
                                  .andReturn();

        // Then
        BookDto[] actual =
                objectMapper.readValue(result.getResponse().getContentAsString(),
                        BookDto[].class);
        assertNotNull(actual);
        assertEquals(expected.size(), actual.length);
        assertEquals(expected, Arrays.stream(actual).toList());
    }

    @Test
    @DisplayName("Search books by params")
    @WithMockUser
    @Sql(scripts = {
            "classpath:database/books/add-categories.sql",
            "classpath:database/books/add-three-books.sql",
            "classpath:database/books/add-categories-for-books.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void searchBooks_SearchExistingBooksByParams_ReturnsExpectedBooks() throws Exception {
        // Given
        List<BookDto> expected = List.of(getOrwell(EXISTING_TITLE), getMelville());

        // When
        MvcResult result = mockMvc.perform(get(URI + "/search")
                                          .param("page", "0")
                                          .param("size", "3")
                                          .param("sort", "id")
                                          .param("titles", "moby", "98")
                                          .param("categoryIds", "1")
                                          .contentType(MediaType.APPLICATION_JSON))
                                  .andExpect(status().isOk())
                                  .andReturn();

        // Then
        BookDto[] actual =
                objectMapper.readValue(result.getResponse().getContentAsString(),
                        BookDto[].class);
        System.out.println(Arrays.stream(actual).toList());
        assertNotNull(actual);
        assertEquals(expected.size(), actual.length);
    }

    @Test
    @DisplayName("Delete existing book")
    @WithMockUser(username = "admin", roles = {"MANAGER"})
    @Sql(scripts = {
            "classpath:database/books/add-categories.sql",
            "classpath:database/books/add-three-books.sql",
            "classpath:database/books/add-categories-for-books.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void deleteBook_DeleteExisingBook_SuccessfullyDeleted() throws Exception {
        // Given
        String url = URI + "/" + EXISTING_ID;

        // When
        mockMvc.perform(delete(url)
                       .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isNoContent())
               .andReturn();
    }

    private static CreateBookRequestDto getRequestDto(String title) {
        return CreateBookRequestDto.builder().title(title).author(EXISTING_AUTHOR)
                                   .isbn(EXISTING_ISBN).categoryIds(EXISTING_CATEGORY_IDS)
                                   .price(BigDecimal.valueOf(PRICE)).build();
    }

    private static BookDto getOrwell(String title) {
        return BookDto.builder().id(EXISTING_ID).title(title).author(EXISTING_AUTHOR)
                      .isbn(EXISTING_ISBN).categoryIds(EXISTING_CATEGORY_IDS)
                      .price(BigDecimal.valueOf(PRICE)).build();
    }

    private static BookDto getMelville() {
        return BookDto.builder().id(2L).title("Moby-Dick").author("Herman Melville")
                      .categoryIds(Set.of(1L, 3L)).isbn("9781503280781")
                      .price(BigDecimal.valueOf(14.99)).build();
    }
}
