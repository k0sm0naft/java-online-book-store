package ua.bookstore.online.repository;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@Getter
@RequiredArgsConstructor
public abstract class AbstractSpecificationProvider<T> implements SpecificationProvider<T> {
    private final Parameter parameter;

    @Override
    public Specification<T> getSpecification(String params) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                        criteriaBuilder.lower(root.get(parameter.getName())),
                        '%' + params.toLowerCase() + '%');
    }
}
