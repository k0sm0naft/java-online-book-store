package ua.bookstore.online.repository.book.specification.provider;

import org.springframework.stereotype.Component;
import ua.bookstore.online.model.Book;
import ua.bookstore.online.repository.AbstractSpecificationProvider;
import ua.bookstore.online.repository.Parameter;

@Component
public class DescriptionSpecificationProvider extends AbstractSpecificationProvider<Book> {
    private DescriptionSpecificationProvider() {
        super(Parameter.DESCRIPTION);
    }
}
