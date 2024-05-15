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
import static ua.bookstore.online.utils.ConstantAndMethod.ADD_CATEGORIES_FOR_BOOKS_SQL;
import static ua.bookstore.online.utils.ConstantAndMethod.ADD_CATEGORIES_SQL;
import static ua.bookstore.online.utils.ConstantAndMethod.ADD_THREE_BOOKS_SQL;
import static ua.bookstore.online.utils.ConstantAndMethod.ID_1;
import static ua.bookstore.online.utils.ConstantAndMethod.ISBN_ORWELL;
import static ua.bookstore.online.utils.ConstantAndMethod.NON_EXISTING_ISBN;
import static ua.bookstore.online.utils.ConstantAndMethod.createBookRequestDto;
import static ua.bookstore.online.utils.ConstantAndMethod.getMelville;
import static ua.bookstore.online.utils.ConstantAndMethod.getNewOrwell;
import static ua.bookstore.online.utils.ConstantAndMethod.getOrwell;
import static ua.bookstore.online.utils.ConstantAndMethod.getRequestDto;
import static ua.bookstore.online.utils.ConstantAndMethod.tearDown;

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
    @DisplayName("Create new book")
    @WithMockUser(username = "admin", roles = {"MANAGER"})
    void createBook_CreateNewBook_ReturnsExpectedBook() throws Exception {
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
    @DisplayName("Create new book with existing ISBN, returns problem detail")
    @WithMockUser(username = "admin", roles = {"MANAGER"})
    void createBook_CreateNewBookWithExistingIsbn_ReturnsProblemDetail() throws Exception {
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
    @DisplayName("Update existing book")
    @WithMockUser(username = "admin", roles = {"MANAGER"})
    void updateBook_UpdateExisingBook_ReturnsExpectedBook() throws Exception {
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
    @DisplayName("Get existing book by ID")
    @WithMockUser
    void getBookById_GetExistingBook_ReturnsExpectedBook() throws Exception {
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
    @DisplayName("Get two existing books")
    @WithMockUser
    void getAll_GetTwoExistingBooks_ReturnsExpectedBooks() throws Exception {
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
    @DisplayName("Search books by params")
    @WithMockUser
    void searchBooks_SearchExistingBooksByParams_ReturnsExpectedBooks() throws Exception {
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
        System.out.println(Arrays.stream(actual).toList());
        assertNotNull(actual);
        assertEquals(expected.size(), actual.length);
    }

    @Test
    @DisplayName("Delete existing book")
    @WithMockUser(username = "admin", roles = {"MANAGER"})
    void deleteBook_DeleteExisingBook_SuccessfullyDeleted() throws Exception {
        // Given
        String url = URI + "/" + ID_1;

        // When
        mockMvc.perform(delete(url)
                       .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();
    }
}
