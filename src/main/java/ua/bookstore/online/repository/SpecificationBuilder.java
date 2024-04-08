package ua.bookstore.online.repository;

import org.springframework.data.jpa.domain.Specification;
import ua.bookstore.online.dto.BookSearchParameters;

public interface SpecificationBuilder<T> {
    Specification<T> build(BookSearchParameters parameters);
}
