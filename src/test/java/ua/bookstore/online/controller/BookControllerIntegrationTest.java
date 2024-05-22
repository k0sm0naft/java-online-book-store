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
import static ua.bookstore.online.utils.TestDataUtils.ADD_CATEGORIES_FOR_BOOKS_SQL;
import static ua.bookstore.online.utils.TestDataUtils.ADD_CATEGORIES_SQL;
import static ua.bookstore.online.utils.TestDataUtils.ADD_THREE_BOOKS_SQL;
import static ua.bookstore.online.utils.TestDataUtils.ID_1;
import static ua.bookstore.online.utils.TestDataUtils.ISBN_ORWELL;
import static ua.bookstore.online.utils.TestDataUtils.NON_EXISTING_ISBN;
import static ua.bookstore.online.utils.TestDataUtils.createBookRequestDto;
import static ua.bookstore.online.utils.TestDataUtils.getMelville;
import static ua.bookstore.online.utils.TestDataUtils.getNewOrwell;
import static ua.bookstore.online.utils.TestDataUtils.getOrwell;
import static ua.bookstore.online.utils.TestDataUtils.getRequestDto;
import static ua.bookstore.online.utils.TestDataUtils.tearDown;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;
import ua.bookstore.online.dto.book.BookDto;
import ua.bookstore.online.dto.book.CreateBookRequestDto;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookControllerIntegrationTest {
    private static final String URI = "/books";
    private static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @BeforeEach
    void beforeEach(@Autowired DataSource dataSource) throws SQLException {
        tearDown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection, new ClassPathResource(ADD_CATEGORIES_SQL));
            ScriptUtils.executeSqlScript(connection, new ClassPathResource(ADD_THREE_BOOKS_SQL));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(ADD_CATEGORIES_FOR_BOOKS_SQL));
        }
    }

    @AfterEach
    void afterEach(@Autowired DataSource dataSource) {
        tearDown(dataSource);
    }

    @Test
    @DisplayName("Create new book, expected: status - 201, response - BookDto")
    @WithMockUser(username = "admin", roles = {"MANAGER"})
    void createBook_CreateNewBook_ReturnsExpectedBookDto() throws Exception {
        // Given
        CreateBookRequestDto requestDto = createBookRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        jsonRequest = jsonRequest.replace(ISBN_ORWELL, NON_EXISTING_ISBN);

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
        assertTrue(EqualsBuilder
                .reflectionEquals(getOrwell(), actual, "id", "isbn", "categoryIds"));
        assertNotNull(actual.id());
        assertEquals(NON_EXISTING_ISBN, actual.isbn());
        assertTrue(requestDto.categoryIds().containsAll(actual.categoryIds()));
    }

    @Test
    @DisplayName("Create new book with existing ISBN, expected: status - 409, response - ProblemDetail")
    @WithMockUser(username = "admin", roles = {"MANAGER"})
    void createBook_CreateNewBookWithExistingIsbn_RespondConflictAndReturnsProblemDetail()
            throws Exception {
        // Given
        CreateBookRequestDto requestDto = createBookRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        MvcResult result = mockMvc.perform(post(URI)
                                          .content(jsonRequest)
                                          .contentType(MediaType.APPLICATION_JSON))
                                  .andExpect(status().isConflict())
                                  .andReturn();

        // Then
        ProblemDetail actual =
                objectMapper.readValue(result.getResponse().getContentAsString(),
                        ProblemDetail.class);
        assertNotNull(actual);
        assertEquals("Conflict", actual.getTitle());
        assertNotNull(actual.getInstance());
        assertEquals(URI, actual.getInstance().getPath());
        assertNotNull(actual.getProperties());
        assertEquals("Non uniq ISBN: " + ISBN_ORWELL, actual.getProperties().get("error"));
    }

    @Test
    @DisplayName("Update existing book, expected: status - 202, response - BookDto")
    @WithMockUser(username = "admin", roles = {"MANAGER"})
    void updateBook_UpdateExisingBook_ReturnsExpectedBookDto() throws Exception {
        // Given
        String updatedTitle = "updatedTitle";
        CreateBookRequestDto requestDto = getRequestDto(updatedTitle);
        BookDto expected = getNewOrwell(updatedTitle);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        String url = URI + "/" + ID_1;

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
    @DisplayName("Get existing book by ID, expected: status - 200, response - BookDto")
    @WithMockUser
    void getBookById_GetExistingBook_ReturnsExpectedBookDto() throws Exception {
        // Given
        BookDto expected = getOrwell();
        String url = URI + "/" + ID_1;

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
    @DisplayName("Get two existing books, expected: status - 200, response - BookDto[]")
    @WithMockUser
    void getAll_GetTwoExistingBooks_ReturnsExpectedBookDtos() throws Exception {
        // Given
        List<BookDto> expected = List.of(getOrwell(), getMelville());

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
    @DisplayName("Search books by params, expected: status - 200, response - BookDto[]")
    @WithMockUser
    void searchBooks_SearchExistingBooksByParams_ReturnsExpectedBookDtos() throws Exception {
        // Given
        List<BookDto> expected = List.of(getOrwell(), getMelville());

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
        assertNotNull(actual);
        assertEquals(expected.size(), actual.length);
    }

    @Test
    @DisplayName("Successfully delete existing book, expected: status - 204")
    @WithMockUser(username = "admin", roles = {"MANAGER"})
    void deleteBook_DeleteExisingBook_RespondNoContent() throws Exception {
        // Given
        String url = URI + "/" + ID_1;

        // When
        mockMvc.perform(delete(url)
                       .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();
    }
}
