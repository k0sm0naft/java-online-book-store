package ua.bookstore.online.repository;

import org.springframework.data.jpa.domain.Specification;

public interface SpecificationProvider<T> {
    String getSearchParameter();

    Specification<T> getSpecification(String[] params);
}
