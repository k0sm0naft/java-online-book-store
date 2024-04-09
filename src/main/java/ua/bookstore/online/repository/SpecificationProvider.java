package ua.bookstore.online.repository;

import org.springframework.data.jpa.domain.Specification;

public interface SpecificationProvider<T, P> {
    P getSearchParameter();

    Specification<T> getSpecification(String[] params);
}
