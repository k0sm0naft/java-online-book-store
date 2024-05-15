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
import static ua.bookstore.online.utils.ConstantAndMethod.ADD_THREE_CATEGORIES_SQL;
import static ua.bookstore.online.utils.ConstantAndMethod.ID_1;
import static ua.bookstore.online.utils.ConstantAndMethod.beforeEachBookRepositoryTest;
import static ua.bookstore.online.utils.ConstantAndMethod.getAdventure;
import static ua.bookstore.online.utils.ConstantAndMethod.getClassic;
import static ua.bookstore.online.utils.ConstantAndMethod.getFiction;
import static ua.bookstore.online.utils.ConstantAndMethod.getMelville;
import static ua.bookstore.online.utils.ConstantAndMethod.getOrwell;
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
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
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
    private static final String URI = "/categories";
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
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(ADD_THREE_CATEGORIES_SQL));
        }
    }

    @AfterEach
    void afterEach(@Autowired DataSource dataSource) {
        tearDown(dataSource);
    }

    @Test
    @DisplayName("Create new category")
    @WithMockUser(username = "admin", roles = {"MANAGER"})
    void createCategory_CreateNewCategory_ReturnsExpectedCategory() throws Exception {
        // Given
        CategoryRequestDto requestDto = new CategoryRequestDto("Classic", "Classic description");
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
    void updateCategory_UpdateExistingCategory_ReturnsExpectedCategory() throws Exception {
        // Given
        String updatedName = "updatedName";
        String updatedDescription = "updatedDescription";
        CategoryRequestDto requestDto = new CategoryRequestDto(updatedName, updatedDescription);
        CategoryResponseDto expected = CategoryResponseDto.builder()
                                                          .id(ID_1)
                                                          .name(updatedName)
                                                          .description(updatedDescription)
                                                          .build();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        String url = URI + "/" + ID_1;

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
    void getCategoryById_GetExistingCategory_ReturnsExpectedCategory() throws Exception {
        // Given
        String url = URI + "/" + ID_1;
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
    void getBooksByCategoryId_GetAllBooksByCategory_ReturnsExpectedBooks(
            @Autowired DataSource dataSource) throws Exception {
        // Given
        beforeEachBookRepositoryTest(dataSource);
        BookDto orwell = getOrwell();
        BookDto melville = getMelville();
        List<BookDto> expected = List.of(orwell, melville);
        String url = URI + "/" + ID_1 + "/books";

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
        assertEquals(orwell.title(), actual[0].title());
        assertEquals(melville.title(), actual[1].title());
    }

    @Test
    @DisplayName("Delete existing category")
    @WithMockUser(username = "admin", roles = {"MANAGER"})
    void deleteCategory_DeleteExistingCategory_SuccessfullyDeleted() throws Exception {
        // Given
        String url = URI + "/" + ID_1;

        // When
        mockMvc.perform(delete(url)
                       .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();
    }
}
