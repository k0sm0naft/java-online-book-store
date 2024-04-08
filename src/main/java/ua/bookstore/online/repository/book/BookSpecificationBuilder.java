package ua.bookstore.online.repository.book;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ua.bookstore.online.dto.BookSearchParameters;
import ua.bookstore.online.model.Book;
import ua.bookstore.online.repository.SpecificationBuilder;
import ua.bookstore.online.repository.SpecificationProviderManager;

@RequiredArgsConstructor
@Component
public class BookSpecificationBuilder implements SpecificationBuilder<Book> {
    private final SpecificationProviderManager<Book> bookSpecificationProviderManager;

    @Override
    public Specification<Book> build(BookSearchParameters bookSearchParameters) {
        Specification<Book> specification = Specification.where(null);
        String[] titles = bookSearchParameters.getTitles();
        if (titles != null && titles.length > 0) {
            specification = specification.and(bookSpecificationProviderManager
                    .getSpecificationProvider(BookSearchParameter.TITLE.getName())
                    .getSpecification(titles));
        }
        String[] authors = bookSearchParameters.getAuthors();
        if (authors != null && authors.length > 0) {
            specification = specification.and(bookSpecificationProviderManager
                    .getSpecificationProvider(BookSearchParameter.AUTHOR.getName())
                    .getSpecification(authors));
        }
        String[] isbns = bookSearchParameters.getIsbns();
        if (isbns != null && isbns.length > 0) {
            specification = specification.and(bookSpecificationProviderManager
                    .getSpecificationProvider(BookSearchParameter.ISBN.getName())
                    .getSpecification(isbns));
        }
        return specification;
    }
}
