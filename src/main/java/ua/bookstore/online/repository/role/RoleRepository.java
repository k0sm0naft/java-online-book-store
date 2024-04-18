package ua.bookstore.online.repository.role;

import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.bookstore.online.model.Role;
import ua.bookstore.online.model.RoleName;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Set<Role> getAllByNameIn(Set<RoleName> roleNames);
}
