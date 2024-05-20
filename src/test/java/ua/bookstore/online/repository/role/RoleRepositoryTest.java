package ua.bookstore.online.repository.role;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ua.bookstore.online.utils.TestDataUtils.ADD_ROLES;
import static ua.bookstore.online.utils.TestDataUtils.CLASSPATH;
import static ua.bookstore.online.utils.TestDataUtils.tearDown;

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
    @DisplayName("Find all roles from set of role names, returns set of roles")
    @Sql(scripts = {
            CLASSPATH + ADD_ROLES
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getAllByNameIn_FindingRolesFromSetOfRoleNames_ReturnsSetOfRoles() {
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
