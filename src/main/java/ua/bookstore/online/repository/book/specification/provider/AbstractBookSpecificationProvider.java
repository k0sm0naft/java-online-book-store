package ua.bookstore.online.repository.book.specification.provider;

import jakarta.persistence.criteria.Predicate;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import ua.bookstore.online.repository.SpecificationProvider;

@Getter
@RequiredArgsConstructor
public abstract class AbstractBookSpecificationProvider<T> implements SpecificationProvider<T> {
    private final String searchParameter;

    @Override
    public Specification<T> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.or(
                        Arrays.stream(params)
                              .map(p -> criteriaBuilder.like(
                                      criteriaBuilder.lower(root.get(searchParameter)),
                                      '%' + p.toLowerCase() + '%')).toArray(Predicate[]::new));
    }
}
