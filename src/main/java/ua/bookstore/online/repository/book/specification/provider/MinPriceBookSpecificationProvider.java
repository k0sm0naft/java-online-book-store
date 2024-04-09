package ua.bookstore.online.repository.book.specification.provider;

import java.math.BigDecimal;
import java.util.Arrays;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ua.bookstore.online.model.Book;
import ua.bookstore.online.repository.book.BookSearchParameter;

@Component
public class MinPriceBookSpecificationProvider extends AbstractBookSpecificationProvider<Book> {
    private static final BookSearchParameter searchParameter = BookSearchParameter.MIN_PRICE;

    public MinPriceBookSpecificationProvider() {
        super(searchParameter);
    }

    @Override
    public Specification<Book> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get(searchParameter.getName()),
                        Arrays.stream(params).mapToInt(Integer::parseInt)
                              .mapToObj(BigDecimal::valueOf).findFirst().get());
    }
}
