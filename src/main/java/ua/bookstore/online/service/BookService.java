package ua.bookstore.online.service;

import java.util.List;
import org.springframework.data.domain.Pageable;
import ua.bookstore.online.dto.book.BookDto;
import ua.bookstore.online.dto.book.BookDtoWithoutCategoryIds;
import ua.bookstore.online.dto.book.CreateBookRequestDto;
import ua.bookstore.online.dto.search.parameters.BookSearchParameters;

public interface BookService {
    BookDto save(CreateBookRequestDto bookRequestDto);

    List<BookDto> getAll(Pageable pageable);

    BookDto getById(Long id);

    List<BookDto> getByParameters(BookSearchParameters searchParameters,
            Pageable pageable);

    BookDto update(Long id, CreateBookRequestDto bookRequestDto);

    void delete(Long id);

    List<BookDtoWithoutCategoryIds> getByCategoryId(Long id, Pageable pageable);
}
