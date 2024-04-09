package ua.bookstore.online.repository.book.specification.provider;

import jakarta.persistence.criteria.Predicate;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import ua.bookstore.online.repository.SpecificationProvider;
import ua.bookstore.online.repository.book.BookSearchParameter;

@RequiredArgsConstructor
public abstract class AbstractBookSpecificationProvider<T>
        implements SpecificationProvider<T, BookSearchParameter> {
    private final BookSearchParameter searchParameter;

    @Override
    public BookSearchParameter getSearchParameter() {
        return searchParameter;
    }

    @Override
    public Specification<T> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.or(
                        Arrays.stream(params)
                              .map(p -> criteriaBuilder.like(
                                      criteriaBuilder.lower(root.get(searchParameter.getName())),
                                      '%' + p.toLowerCase() + '%')).toArray(Predicate[]::new));
    }
}
