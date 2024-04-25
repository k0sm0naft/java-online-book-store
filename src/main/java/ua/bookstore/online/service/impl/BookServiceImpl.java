package ua.bookstore.online.service.impl;

import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.bookstore.online.dto.book.BookDto;
import ua.bookstore.online.dto.book.BookDtoWithoutCategoryIds;
import ua.bookstore.online.dto.book.CreateBookRequestDto;
import ua.bookstore.online.dto.search.parameters.BookSearchParameters;
import ua.bookstore.online.exception.EntityNotFoundException;
import ua.bookstore.online.exception.UniqueIsbnException;
import ua.bookstore.online.mapper.BookMapper;
import ua.bookstore.online.model.Book;
import ua.bookstore.online.repository.book.BookRepository;
import ua.bookstore.online.repository.book.BookSpecificationBuilder;
import ua.bookstore.online.service.BookService;
import ua.bookstore.online.service.CategoryService;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookSpecificationBuilder bookSpecificationBuilder;
    private final CategoryService categoryService;

    @Transactional
    @Override
    public BookDto save(CreateBookRequestDto bookRequestDto) {
        if (bookRepository.findByIsbn(bookRequestDto.isbn()).isPresent()) {
            throw new UniqueIsbnException("Non uniq ISBN: " + bookRequestDto.isbn());
        }
        validateCategories(bookRequestDto);
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
        Book book = bookRepository.findByIdWithCategories(id).orElseThrow(
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

    @Transactional
    @Override
    public BookDto update(Long id, CreateBookRequestDto bookRequestDto) {
        validateIsbnUniqueness(id, bookRequestDto);

        Book book = bookMapper.toModel(bookRequestDto);
        book.setId(id);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Transactional
    @Override
    public void delete(Long id) {
        if (bookRepository.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Can't find book to delete by id " + id);
        }
        bookRepository.deleteById(id);
    }

    @Override
    public List<BookDtoWithoutCategoryIds> getByCategoryId(Long id, Pageable pageable) {
        return bookRepository.findAllByCategoryId(id, pageable).stream()
                .map(bookMapper::toDtoWithoutCategories)
                .toList();
    }

    private void validateIsbnUniqueness(Long id, CreateBookRequestDto bookRequestDto) {
        List<Book> allByIdOrIsbn = bookRepository.findAllByIdOrIsbn(id, bookRequestDto.isbn());
        if (allByIdOrIsbn.size() > 1) {
            throw new UniqueIsbnException("Book with ISBN "
                    + bookRequestDto.isbn() + " already exist");
        }

        if (!allByIdOrIsbn.getFirst().getId().equals(id)) {
            throw new EntityNotFoundException("Can't find book to update by id " + id);
        }

        validateCategories(bookRequestDto);
    }

    private void validateCategories(CreateBookRequestDto bookRequestDto) {
        Set<Long> categoryIds = bookRequestDto.categoryIds();
        Set<Long> categoryIdsFromDb = categoryService.getAllExistedCategoryIdsFromIds(categoryIds);
        System.out.println(categoryIdsFromDb);
        if (categoryIdsFromDb.size() < categoryIds.size()) {
            List<Long> notExistedIds = categoryIds.stream()
                                                  .filter(id -> !categoryIdsFromDb.contains(id))
                                                  .toList();
            throw new EntityNotFoundException("Can't find categories with ids: " + notExistedIds);
        }
    }
}
