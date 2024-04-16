package ua.bookstore.online.repository;

import org.springframework.data.jpa.domain.Specification;
import ua.bookstore.online.dto.search.parameters.AbstractSearchParameters;

public interface SpecificationBuilder<T, P extends AbstractSearchParameters> {
    Specification<T> build(P parameters);
}
