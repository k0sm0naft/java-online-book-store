package ua.bookstore.online.repository.book;

import java.util.Arrays;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ua.bookstore.online.dto.BookSearchParameters;
import ua.bookstore.online.model.Book;
import ua.bookstore.online.repository.SpecificationBuilder;
import ua.bookstore.online.repository.SpecificationProviderManager;

@RequiredArgsConstructor
@Component
public class BookSpecificationBuilder implements SpecificationBuilder<Book, BookSearchParameters> {
    private final SpecificationProviderManager<Book>
            bookSpecificationProviderManager;

    @Override
    public Specification<Book> build(BookSearchParameters bookSearchParameters) {
        return Arrays.stream(BookSearchParameter.values())
                     .map(p -> getSpecification(bookSearchParameters, p))
                     .reduce(Specification.where(null), Specification::and);
    }

    private Specification<Book> getSpecification(BookSearchParameters bookSearchParameters,
            BookSearchParameter parameter) {

        String[] parameters = switch (parameter) {
            case TITLE -> bookSearchParameters.getTitles();
            case AUTHOR -> bookSearchParameters.getAuthors();
            case ISBN -> bookSearchParameters.getIsbns();
            case PRICE -> {
                Long minPrice = Optional.ofNullable(
                        bookSearchParameters.getMinPrice()).orElse(0L);
                Long maxPrice = Optional.ofNullable(
                        bookSearchParameters.getMaxPrice()).orElse(Long.MAX_VALUE);
                yield new String[]{String.valueOf(minPrice), String.valueOf(maxPrice)};
            }
        };
        return isValidParameters(parameters)
                ? bookSpecificationProviderManager.getSpecificationProvider(parameter.getName())
                                                  .getSpecification(parameters) : null;
    }

    private boolean isValidParameters(String[] parameters) {
        return parameters != null && parameters.length > 0;
    }
}
