package ua.bookstore.online.repository.book.specification.provider;

import org.springframework.stereotype.Component;
import ua.bookstore.online.model.Book;
import ua.bookstore.online.repository.book.BookSearchParameter;

@Component
public class TitleBookSpecificationProvider extends AbstractBookSpecificationProvider<Book> {
    private TitleBookSpecificationProvider() {
        super(BookSearchParameter.TITLE);
    }
}
