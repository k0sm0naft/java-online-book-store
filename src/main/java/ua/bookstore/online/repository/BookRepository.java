package ua.bookstore.online.repository;

import java.util.List;
import ua.bookstore.online.model.Book;

public interface BookRepository {
    Book save(Book book);

    List<Book> findAll();
}
