package ua.bookstore.online.service;

import java.util.List;
import ua.bookstore.online.dto.BookDto;
import ua.bookstore.online.dto.CreateBookRequestDto;
import ua.bookstore.online.dto.SearchParameters;

public interface BookService {
    BookDto save(CreateBookRequestDto bookRequestDto);

    List<BookDto> getAll();

    BookDto getById(Long id);

    List<BookDto> getByParameters(SearchParameters searchParameters);

    BookDto update(Long id, CreateBookRequestDto bookRequestDto);

    void delete(Long id);
}
