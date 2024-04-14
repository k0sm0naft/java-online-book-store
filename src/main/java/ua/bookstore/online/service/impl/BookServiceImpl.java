package ua.bookstore.online.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ua.bookstore.online.dto.book.BookDto;
import ua.bookstore.online.dto.book.CreateBookRequestDto;
import ua.bookstore.online.dto.serch.parameters.BookSearchParameters;
import ua.bookstore.online.exception.EntityNotFoundException;
import ua.bookstore.online.exception.UniqIsbnException;
import ua.bookstore.online.mapper.BookMapper;
import ua.bookstore.online.model.Book;
import ua.bookstore.online.repository.book.BookRepository;
import ua.bookstore.online.repository.book.BookSpecificationBuilder;
import ua.bookstore.online.service.BookService;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookSpecificationBuilder bookSpecificationBuilder;

    @Override
    public BookDto save(CreateBookRequestDto bookRequestDto) {
        if (bookRepository.findByIsbn(bookRequestDto.isbn()).isPresent()) {
            throw new UniqIsbnException("Non uniq ISBN: " + bookRequestDto.isbn());
        }
        Book book = bookMapper.toModel(bookRequestDto);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public List<BookDto> getAll(Pageable pageable) {
        return bookRepository.findAll(pageable).stream()
                             .map(bookMapper::toDto)
                             .toList();
    }

    @Override
    public BookDto getById(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Entity not found by id " + id));
        return bookMapper.toDto(book);
    }

    @Override
    public List<BookDto> getByParameters(
            BookSearchParameters bookSearchParameters, Pageable pageable) {
        Specification<Book> bookSpecification =
                bookSpecificationBuilder.build(bookSearchParameters);
        return bookRepository.findAll(bookSpecification, pageable).stream()
                             .map(bookMapper::toDto)
                             .toList();
    }

    @Override
    public BookDto update(Long id, CreateBookRequestDto bookRequestDto) {
        List<Book> allByIdOrIsbn = bookRepository.findAllByIdOrIsbn(id, bookRequestDto.isbn());

        if (allByIdOrIsbn.size() > 1) {
            throw new UniqIsbnException("Book with ISBN "
                    + bookRequestDto.isbn() + " already exist");
        }

        if (!allByIdOrIsbn.get(0).getId().equals(id)) {
            throw new EntityNotFoundException("Can't find book to update by id " + id);
        }

        Book book = bookMapper.toModel(bookRequestDto);
        book.setId(id);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public void delete(Long id) {
        if (bookRepository.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Can't find book to delete by id " + id);
        }
        bookRepository.deleteById(id);
    }
}
