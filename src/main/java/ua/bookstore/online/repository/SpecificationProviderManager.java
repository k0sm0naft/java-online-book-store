package ua.bookstore.online.repository;

public interface SpecificationProviderManager<T> {
    SpecificationProvider<T> getSpecificationProvider(Parameter parameter);
}
