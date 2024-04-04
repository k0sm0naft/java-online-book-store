package ua.bookstore.online.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.bookstore.online.dto.BookDto;
import ua.bookstore.online.dto.CreateBookRequestDto;
import ua.bookstore.online.exception.EntityNotFoundException;
import ua.bookstore.online.mapper.BookMapper;
import ua.bookstore.online.model.Book;
import ua.bookstore.online.repository.BookRepository;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public BookDto save(CreateBookRequestDto bookRequestDto) {
        Book book = bookMapper.toModel(bookRequestDto);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public List<BookDto> getAll() {
        return bookRepository.getAll().stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public BookDto getById(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Entity not found by id " + id));
        return bookMapper.toDto(book);
    }
}
