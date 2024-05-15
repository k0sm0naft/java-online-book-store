package ua.bookstore.online.repository.role;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ua.bookstore.online.utils.ConstantAndMethod.ADD_ROLES;
import static ua.bookstore.online.utils.ConstantAndMethod.CLASSPATH;
import static ua.bookstore.online.utils.ConstantAndMethod.tearDown;

import java.util.Set;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import ua.bookstore.online.model.Role;
import ua.bookstore.online.model.RoleName;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RoleRepositoryTest {
    @Autowired
    private RoleRepository roleRepository;

    @BeforeAll
    public static void beforeAll(@Autowired DataSource dataSource) {
        tearDown(dataSource);
    }

    @Test
    @DisplayName("Find user by email with roles")
    @Sql(scripts = {
            CLASSPATH + ADD_ROLES
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findByEmail_FindingUserByEmail_ReturnsOptionalOfUserWithRoles() {
        // Given
        RoleName roleNameAdmin = RoleName.ROLE_ADMIN;
        RoleName roleNameUser = RoleName.ROLE_USER;
        Set<RoleName> roleNames = Set.of(roleNameAdmin, roleNameUser);
        Role userRole = new Role();
        userRole.setName(roleNameUser);
        Role adminRole = new Role();
        adminRole.setName(roleNameAdmin);
        Set<Role> expected = Set.of(userRole, adminRole);

        // When
        Set<Role> actual = roleRepository.getAllByNameIn(roleNames);

        // Then
        assertEquals(expected.size(), actual.size());
    }
}
