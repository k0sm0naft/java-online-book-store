package ua.bookstore.online.repository.book;

import java.util.Arrays;
import java.util.stream.Stream;
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
    private final SpecificationProviderManager<Book, BookSearchParameter>
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
            case MIN_PRICE -> Stream.of(bookSearchParameters.getMinPrice() == null
                                            ? 0
                                            : bookSearchParameters.getMinPrice())
                                    .map(String::valueOf)
                                    .toArray(String[]::new);
            case MAX_PRICE -> Stream.of(bookSearchParameters.getMaxPrice() == null
                                            ? Integer.MAX_VALUE
                                            : bookSearchParameters.getMaxPrice())
                                    .map(String::valueOf)
                                    .toArray(String[]::new);
        };
        return isValidParameters(parameters)
                ? bookSpecificationProviderManager.getSpecificationProvider(parameter)
                                                  .getSpecification(parameters) : null;
    }

    private boolean isValidParameters(String[] parameters) {
        return parameters != null && parameters.length > 0;
    }
}
