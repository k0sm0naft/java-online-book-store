package ua.bookstore.online.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.bookstore.online.model.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
}
