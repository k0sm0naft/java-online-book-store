package ua.bookstore.online.repository.category;

import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.bookstore.online.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Set<Category> findAllByIdIn(Set<Long> ids);
}
