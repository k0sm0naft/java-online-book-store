package ua.bookstore.online.repository.book;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ua.bookstore.online.dto.SearchParameters;
import ua.bookstore.online.model.Book;
import ua.bookstore.online.repository.Parameter;
import ua.bookstore.online.repository.SpecificationBuilder;
import ua.bookstore.online.repository.SpecificationProviderManager;

@RequiredArgsConstructor
@Component
public class BookSpecificationBuilder implements SpecificationBuilder<Book> {
    private final SpecificationProviderManager<Book> bookSpecificationProviderManager;

    @Override
    public Specification<Book> build(SearchParameters parameters) {
        Specification<Book> specification = Specification.where(null);
        if (parameters.title() != null) {
            specification = specification.and(bookSpecificationProviderManager
                    .getSpecificationProvider(Parameter.TITLE)
                    .getSpecification(parameters.title()));
        }
        if (parameters.author() != null) {
            specification = specification.and(bookSpecificationProviderManager
                    .getSpecificationProvider(Parameter.AUTHOR)
                    .getSpecification(parameters.author()));
        }
        if (parameters.isbn() != null) {
            specification = specification.and(bookSpecificationProviderManager
                    .getSpecificationProvider(Parameter.ISBN)
                    .getSpecification(parameters.isbn()));
        }
        if (parameters.description() != null) {
            specification = specification.and(bookSpecificationProviderManager
                    .getSpecificationProvider(Parameter.DESCRIPTION)
                    .getSpecification(parameters.description()));
        }
        return specification;
    }
}
