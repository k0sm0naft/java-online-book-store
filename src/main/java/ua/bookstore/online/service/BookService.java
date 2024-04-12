package ua.bookstore.online.service;

import java.util.List;
import org.springframework.data.domain.Pageable;
import ua.bookstore.online.dto.BookDto;
import ua.bookstore.online.dto.BookSearchParameters;
import ua.bookstore.online.dto.CreateBookRequestDto;

public interface BookService {
    BookDto save(CreateBookRequestDto bookRequestDto);

    List<BookDto> getAll(Pageable pageable);

    BookDto getById(Long id);

    List<BookDto> getByParameters(BookSearchParameters searchParameters, Pageable pageable);

    BookDto update(Long id, CreateBookRequestDto bookRequestDto);

    void delete(Long id);
}
