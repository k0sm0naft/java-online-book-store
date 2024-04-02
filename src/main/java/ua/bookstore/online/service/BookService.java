package ua.bookstore.online.service;

import java.util.List;
import ua.bookstore.online.model.Book;

public interface BookService {
    Book save(Book book);

    List<Book> findAll();
}
