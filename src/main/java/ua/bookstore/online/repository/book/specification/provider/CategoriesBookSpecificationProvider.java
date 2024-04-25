package ua.bookstore.online.repository.book.specification.provider;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import java.util.Arrays;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ua.bookstore.online.model.Book;
import ua.bookstore.online.model.Category;
import ua.bookstore.online.repository.book.BookSearchParameter;

@Component
public class CategoriesBookSpecificationProvider extends AbstractBookSpecificationProvider<Book> {
    private CategoriesBookSpecificationProvider() {
        super(BookSearchParameter.CATEGORY.getName());
    }

    @Override
    public Specification<Book> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) -> {
            Join<Book, Category> categoryJoin = root.join("categories", JoinType.INNER);
            return categoryJoin.get("id").in(Arrays.stream(params)
                                                              .map(Long::parseLong)
                                                              .toArray());
        };
    }
}
