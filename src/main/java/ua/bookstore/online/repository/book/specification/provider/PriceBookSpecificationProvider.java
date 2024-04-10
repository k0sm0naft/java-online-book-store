package ua.bookstore.online.repository.book.specification.provider;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ua.bookstore.online.model.Book;
import ua.bookstore.online.repository.book.BookSearchParameter;

@Component
public class PriceBookSpecificationProvider extends AbstractBookSpecificationProvider<Book> {
    private static final String searchParameter = BookSearchParameter.PRICE.getName();
    private static final int MIN_PRICE = 0;
    private static final int MAX_PRICE = 1;

    public PriceBookSpecificationProvider() {
        super(searchParameter);
    }

    @Override
    public Specification<Book> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get(searchParameter),
                        params[MIN_PRICE], params[MAX_PRICE]);
    }
}
