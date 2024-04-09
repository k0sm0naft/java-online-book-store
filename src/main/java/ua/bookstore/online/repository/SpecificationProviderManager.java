package ua.bookstore.online.repository;

public interface SpecificationProviderManager<T, P> {
    SpecificationProvider<T, P> getSpecificationProvider(P parameter);
}
