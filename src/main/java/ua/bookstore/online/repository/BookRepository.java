package ua.bookstore.online.repository;

import java.util.List;
import java.util.Optional;
import ua.bookstore.online.model.Book;

public interface BookRepository {
    Book save(Book book);

    List<Book> getAll();

    Optional<Book> findById(Long id);
}
