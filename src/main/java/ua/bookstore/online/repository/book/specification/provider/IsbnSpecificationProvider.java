package ua.bookstore.online.repository.book.specification.provider;

import org.springframework.stereotype.Component;
import ua.bookstore.online.model.Book;
import ua.bookstore.online.repository.AbstractSpecificationProvider;
import ua.bookstore.online.repository.Parameter;

@Component
public class IsbnSpecificationProvider extends AbstractSpecificationProvider<Book> {
    private IsbnSpecificationProvider() {
        super(Parameter.ISBN);
    }
}
