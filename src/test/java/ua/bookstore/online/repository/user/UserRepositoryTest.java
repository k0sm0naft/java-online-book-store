package ua.bookstore.online.repository.user;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ua.bookstore.online.utils.ConstantAndMethod.ADD_ROLES;
import static ua.bookstore.online.utils.ConstantAndMethod.ADD_USERS_ROLES;
import static ua.bookstore.online.utils.ConstantAndMethod.ADD_USERS_SQL;
import static ua.bookstore.online.utils.ConstantAndMethod.CLASSPATH;
import static ua.bookstore.online.utils.ConstantAndMethod.USER_EMAIL;
import static ua.bookstore.online.utils.ConstantAndMethod.getUser;
import static ua.bookstore.online.utils.ConstantAndMethod.tearDown;

import java.util.Optional;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;
import ua.bookstore.online.model.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    public static void beforeAll(@Autowired DataSource dataSource) {
        tearDown(dataSource);
    }

    @Test
    @DisplayName("Find user by email with roles")
    @Sql(scripts = {
            CLASSPATH + ADD_USERS_SQL,
            CLASSPATH + ADD_ROLES,
            CLASSPATH + ADD_USERS_ROLES
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findByEmail_FindingUserByEmail_ReturnsOptionalOfUserWithRoles() {
        // Given
        User expected = getUser();

        // When
        Optional<User> actual = userRepository.findByEmail(USER_EMAIL);

        // Then
        assertTrue(actual.isPresent(), "User should be present");
        System.out.println(expected);
        System.out.println(actual.get());
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual.get(), "password", "roles"));
        assertFalse(actual.get().getRoles().isEmpty());
    }
}
