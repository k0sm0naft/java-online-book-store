package ua.bookstore.online.repository.book;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import ua.bookstore.online.model.Book;

public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
    Optional<Book> findByIsbn(String isbn);

    @Query("FROM Book b JOIN FETCH b.categories WHERE b.id = :id")
    Optional<Book> findByIdWithCategories(Long id);

    @EntityGraph(attributePaths = {"categories"})
    Page<Book> findAll(Specification<Book> specification, Pageable pageable);

    @Query("FROM Book b JOIN FETCH b.categories")
    List<Book> findAllBooks(Pageable pageable);

    @EntityGraph(attributePaths = {"categories"})
    List<Book> findAllByCategories_Id(Long categoryId, Pageable pageable);

    List<Book> findAllByIdOrIsbn(Long id, String isbn);
}
