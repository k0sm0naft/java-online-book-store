package ua.bookstore.online.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ua.bookstore.online.utils.ConstantAndMethod.ADD_ROLES;
import static ua.bookstore.online.utils.ConstantAndMethod.ADD_USERS_ROLES;
import static ua.bookstore.online.utils.ConstantAndMethod.ADD_USERS_SQL;
import static ua.bookstore.online.utils.ConstantAndMethod.CLASSPATH;
import static ua.bookstore.online.utils.ConstantAndMethod.FIRST_NAME;
import static ua.bookstore.online.utils.ConstantAndMethod.LAST_NAME;
import static ua.bookstore.online.utils.ConstantAndMethod.PASSWORD;
import static ua.bookstore.online.utils.ConstantAndMethod.SHIPPING_ADDRESS;
import static ua.bookstore.online.utils.ConstantAndMethod.TEAR_DOWN_DB_SQL;
import static ua.bookstore.online.utils.ConstantAndMethod.USER_EMAIL;
import static ua.bookstore.online.utils.ConstantAndMethod.getUserRequestDto;
import static ua.bookstore.online.utils.ConstantAndMethod.getUserResponseDto;
import static ua.bookstore.online.utils.ConstantAndMethod.tearDown;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;
import ua.bookstore.online.dto.user.UserLoginRequestDto;
import ua.bookstore.online.dto.user.UserLoginResponseDto;
import ua.bookstore.online.dto.user.UserRegistrationRequestDto;
import ua.bookstore.online.dto.user.UserResponseDto;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerIntegrationTest {
    private static final String AUTH_URI = "/auth";
    private static final String REGISTRATION_URI = AUTH_URI + "/registration";
    private static final String LOGIN_URI = AUTH_URI + "/login";
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
        tearDown(dataSource);
    }

    @Test
    @DisplayName("Register new user")
    void register_RegisterNewUser_ReturnsUserResponseDto() throws Exception {
        // Given
        UserRegistrationRequestDto userRequestDto = getUserRequestDto();
        UserResponseDto expected = getUserResponseDto();
        String jsonRequest = objectMapper.writeValueAsString(userRequestDto);

        // When
        MvcResult result = mockMvc.perform(post(REGISTRATION_URI)
                                          .content(jsonRequest)
                                          .contentType(MediaType.APPLICATION_JSON))
                                  .andExpect(status().isCreated())
                                  .andReturn();

        // Then
        UserResponseDto actual =
                objectMapper.readValue(result.getResponse().getContentAsString(),
                        UserResponseDto.class);
        assertNotNull(actual);
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual, "id"));
    }

    @Test
    @DisplayName("Register new user with invalid passwords")
    void register_RegisterNewUserWithInvalidPasswords_RespondBadRequest() throws Exception {
        // Given
        UserRegistrationRequestDto userRequestDto =
                new UserRegistrationRequestDto(USER_EMAIL, PASSWORD, "123456789", FIRST_NAME,
                        LAST_NAME, SHIPPING_ADDRESS);
        UserResponseDto expected = getUserResponseDto();
        String jsonRequest = objectMapper.writeValueAsString(userRequestDto);

        // When
        MvcResult result = mockMvc.perform(post(REGISTRATION_URI)
                                          .content(jsonRequest)
                                          .contentType(MediaType.APPLICATION_JSON))
                                  .andExpect(status().isBadRequest())
                                  .andReturn();

        // Then
        ProblemDetail actual =
                objectMapper.readValue(result.getResponse().getContentAsString(),
                        ProblemDetail.class);
        assertNotNull(actual);
        assertEquals("Bad Request", actual.getTitle());
        assertNotNull(actual.getInstance());
        assertEquals(REGISTRATION_URI, actual.getInstance().getPath());
        assertNotNull(actual.getProperties());
        assertEquals(List.of("Passwords don't match!"), actual.getProperties().get("error"));
    }

    @Test
    @DisplayName("Login existing user")
    @Sql(scripts = {
            CLASSPATH + TEAR_DOWN_DB_SQL,
            CLASSPATH + ADD_USERS_SQL,
            CLASSPATH + ADD_ROLES,
            CLASSPATH + ADD_USERS_ROLES
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void login_LoginExistingUser_ReturnsUserLoginResponseDto() throws Exception {
        // Given
        UserLoginRequestDto userRequestDto = new UserLoginRequestDto(USER_EMAIL, PASSWORD);
        String jsonRequest = objectMapper.writeValueAsString(userRequestDto);

        // When
        MvcResult result = mockMvc.perform(post(LOGIN_URI)
                                          .content(jsonRequest)
                                          .contentType(MediaType.APPLICATION_JSON))
                                  .andExpect(status().isAccepted())
                                  .andReturn();

        // Then
        UserLoginResponseDto actual =
                objectMapper.readValue(result.getResponse().getContentAsString(),
                        UserLoginResponseDto.class);
        assertNotNull(actual);
        String tokenRegex = "^[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+$";
        assertTrue(actual.token().matches(tokenRegex));
    }
}
