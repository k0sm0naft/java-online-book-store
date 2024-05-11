package ua.bookstore.online.service.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static ua.bookstore.online.utils.ConstantAndMethod.AUTHOR;
import static ua.bookstore.online.utils.ConstantAndMethod.CATEGORY_IDS;
import static ua.bookstore.online.utils.ConstantAndMethod.ISBN;
import static ua.bookstore.online.utils.ConstantAndMethod.PRICE;
import static ua.bookstore.online.utils.ConstantAndMethod.TITLE;
import static ua.bookstore.online.utils.ConstantAndMethod.createBook;
import static ua.bookstore.online.utils.ConstantAndMethod.createBookRequestDto;
import static ua.bookstore.online.utils.ConstantAndMethod.getBookDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;
import ua.bookstore.online.dto.book.BookDto;
import ua.bookstore.online.dto.book.BookDtoWithoutCategoryIds;
import ua.bookstore.online.dto.book.CreateBookRequestDto;
import ua.bookstore.online.exception.EntityNotFoundException;
import ua.bookstore.online.exception.UniqueIsbnException;
import ua.bookstore.online.mapper.BookMapper;
import ua.bookstore.online.model.Book;
import ua.bookstore.online.repository.book.BookRepository;
import ua.bookstore.online.repository.book.BookSpecificationBuilder;
import ua.bookstore.online.service.CategoryService;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {
    private static final Long ID = 15L;
    @InjectMocks
    private BookServiceImpl bookService;
    @Mock
    private BookMapper bookMapper;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private CategoryService categoryService;
    @Mock
    private BookSpecificationBuilder bookSpecificationBuilder;

    @Test
    @DisplayName("Save new Book and return BookDto")
    void save_WithUniqueIsbnAndValidCategories_ReturnsBookDto() {
        // Given
        CreateBookRequestDto requestDto = createBookRequestDto();
        Book book = createBook();

        // Mocking behavior
        mockingForSaveMethod(requestDto, CATEGORY_IDS);
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        mockingMapperMethods(book);

        // When
        BookDto actual = bookService.save(requestDto);

        // Then
        assertNotNull(actual);
        BookDto expected = getBookDto(book);
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual));

        // Verify repository method calls
        verify(bookRepository).findByIsbn(requestDto.isbn());
        verifySaveMethods();
        verifyMapperMethods();
        verifyNoMoreInteractions(bookRepository, categoryService, bookMapper);
    }

    @Test
    @DisplayName("Save new Book with non-existing categories trows exception")
    void save_WithNonExistingCategories_ThrowsException() {
        // Given
        CreateBookRequestDto requestDto = createBookRequestDto();
        Set<Long> existedCategoriesIds = Set.of();

        // Mocking behavior
        mockingForSaveMethod(requestDto, existedCategoriesIds);

        // When
        Exception actual =
                assertThrows(EntityNotFoundException.class, () -> bookService.save(requestDto));

        // Then
        assertEquals("Can't find categories with ids: " + CATEGORY_IDS, actual.getMessage());

        // Verify method calls
        verifyNoMoreInteractions(bookRepository, categoryService, bookMapper);
    }

    @Test
    @DisplayName("Save new Book with existing ISBN trows exception")
    void save_WithNonUniqueIsbn_ThrowsException() {
        // Given
        CreateBookRequestDto requestDto = createBookRequestDto();
        Book book = createBook();

        // Mocking behavior
        when(bookRepository.findByIsbn(requestDto.isbn())).thenReturn(Optional.of(book));

        // When
        Exception actual =
                assertThrows(UniqueIsbnException.class, () -> bookService.save(requestDto));

        // Then
        assertEquals("Non uniq ISBN: " + requestDto.isbn(), actual.getMessage());

        // Verify method calls
        verifyNoMoreInteractions(bookMapper, bookRepository, categoryService);
    }

    @Test
    @DisplayName("Get all books from repository")
    void getAll_ReturnsAllBooksFromDb() {
        // Given
        List<Book> booksFromRepository = List.of(createBook(), createBook());

        // Mocking behavior
        when(bookRepository.findAllBooks(any(Pageable.class))).thenReturn(booksFromRepository);
        when(bookMapper.toDto(any(Book.class))).thenReturn(getBookDto(createBook()));

        // When
        List<BookDto> result = bookService.getAll(Pageable.unpaged());

        // Then
        assertEquals(booksFromRepository.size(), result.size());

        // Verify method calls
        verify(bookRepository).findAllBooks(any(Pageable.class));
        verify(bookMapper, times(booksFromRepository.size())).toDto(any(Book.class));
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Get existing book from DB and return BookDto")
    void getById_ExistingId_ReturnsBookDto() {
        // Given
        Book bookFromRepository = createBook();
        bookFromRepository.setId(ID);

        // Mocking behavior
        when(bookRepository.findByIdWithCategories(ID)).thenReturn(Optional.of(bookFromRepository));
        when(bookMapper.toDto(bookFromRepository)).thenReturn(
                getBookDto(bookFromRepository));

        // When
        BookDto result = bookService.getById(ID);

        // Then
        BookDto expected = getBookDto(bookFromRepository);
        assertTrue(EqualsBuilder.reflectionEquals(expected, result));

        // Verify method calls
        verify(bookRepository).findByIdWithCategories(ID);
        verify(bookMapper).toDto(bookFromRepository);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Get non-existing book from DB throws exception")
    void getById_NonExistingId_ThrowsException() {
        // Mocking behavior for repository
        when(bookRepository.findByIdWithCategories(ID)).thenReturn(Optional.empty());

        // When
        Exception actual =
                assertThrows(EntityNotFoundException.class, () -> bookService.getById(ID));

        // Then
        assertEquals("Book not found by id " + ID, actual.getMessage());

        // Verify method calls
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Get by parameter and return filtered list of BoolDto")
    void getByParameters_GetListOfBooks_ReturnsFilteredBookDtos() {
        // Given
        Pageable pageable = Pageable.unpaged();
        Specification<Book> specification = Specification.where(null);

        Page<Book> booksFromRepository = new PageImpl<>(List.of(createBook(), createBook()));

        // Mocking behavior
        when(bookSpecificationBuilder.build(any())).thenReturn(specification);
        when(bookRepository.findAll(specification, pageable)).thenReturn(
                booksFromRepository);
        when(bookMapper.toDto(any(Book.class))).thenReturn(getBookDto(createBook()));

        // When
        List<BookDto> actual = bookService.getByParameters(any(), pageable);

        // Then
        assertEquals(booksFromRepository.getSize(), actual.size());

        // Verify method calls
        verify(bookSpecificationBuilder).build(any());
        verify(bookRepository).findAll(specification, pageable);
        verify(bookMapper, times(booksFromRepository.getSize())).toDto(any());
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Update existing book and unique ISBN and return BookDto")
    void update_ExistingBook_ReturnsBookDto() {
        // Given
        Book existingBook = createBook();
        existingBook.setId(ID);
        existingBook.setPrice(BigDecimal.TEN);

        // Mocking behavior
        mockingForUpdateMethod(existingBook, CATEGORY_IDS);
        when(bookRepository.save(any(Book.class))).thenReturn(createBook());
        mockingMapperMethods(existingBook);

        // When
        BookDto actual = bookService.update(ID, createBookRequestDto());

        // Then
        assertTrue(EqualsBuilder.reflectionEquals(getBookDto(existingBook), actual));

        // Verify repository method calls
        verify(bookRepository).findAllByIdOrIsbn(ID, ISBN);
        verifySaveMethods();
        verifyMapperMethods();
        verifyNoMoreInteractions(bookRepository, categoryService, bookMapper);
    }

    @Test
    @DisplayName("Update non-existing book throws exception")
    void update_NonExistingBook_ThrowsException() {
        // Given
        CreateBookRequestDto requestDto = createBookRequestDto();

        // Mocking behavior
        when(bookRepository.findAllByIdOrIsbn(ID, ISBN)).thenReturn(List.of());

        // When
        Exception actual = assertThrows(EntityNotFoundException.class,
                () -> bookService.update(ID, requestDto));

        // Then
        assertEquals("Can't find book to update by id " + ID, actual.getMessage());

        // Verify method calls
        verifyNoMoreInteractions(bookRepository, categoryService, bookMapper);
    }

    @Test
    @DisplayName("Update with non unique ISBN throws exception")
    void update_NonUniqueIsbn_ThrowsException() {
        // Given
        CreateBookRequestDto requestDto = createBookRequestDto();
        Book existingBook = createBook();
        existingBook.setId(ID);
        Book bookWithSameIsbn = createBook();

        // Mocking behavior
        when(bookRepository.findAllByIdOrIsbn(ID, ISBN)).thenReturn(
                List.of(existingBook, bookWithSameIsbn));

        // When
        Exception actual = assertThrows(UniqueIsbnException.class,
                () -> bookService.update(ID, requestDto));

        // Then
        assertEquals("Book with ISBN " + ISBN + " already exist", actual.getMessage());

        // Verify method calls
        verifyNoMoreInteractions(bookRepository, categoryService, bookMapper);
    }

    @Test
    @DisplayName("Update with invalid categories IDs throws exception")
    void update_NonExistingCategories_ThrowsException() {
        // Given
        CreateBookRequestDto requestDto = createBookRequestDto();
        Book existingBook = createBook();
        existingBook.setId(ID);
        Set<Long> existedCategoriesIds = Set.of();

        // Mocking behavior
        mockingForUpdateMethod(existingBook, existedCategoriesIds);

        // When
        Exception actual =
                assertThrows(EntityNotFoundException.class,
                        () -> bookService.update(ID, requestDto));

        // Then
        assertEquals("Can't find categories with ids: " + CATEGORY_IDS, actual.getMessage());

        // Verify method calls
        verifyNoMoreInteractions(bookRepository, categoryService, bookMapper);
    }

    @Test
    @DisplayName("Delete existing book returns void")
    void delete_ExistingBook_SuccessfullyDeleted() {
        // Mocking behavior
        when(bookRepository.findById(ID)).thenReturn(Optional.of(new Book()));

        // When
        assertDoesNotThrow(() -> bookService.delete(ID));

        // Verify method calls
        verify(bookRepository).findById(ID);
        verify(bookRepository).deleteById(ID);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("Delete non-existing book throws exception")
    void delete_NonExistingBook_ThrowsException() {
        // Mocking behavior
        when(bookRepository.findById(ID)).thenReturn(Optional.empty());

        // When
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> bookService.delete(ID));

        // Then
        assertEquals("Can't find book to delete by id " + ID, exception.getMessage());

        // Verify method calls
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("Get all books by category")
    void getByCategoryId_ReturnsBooksForExistingCategory() {
        // Given
        Pageable pageable = Pageable.unpaged();
        List<Book> booksFromRepository = List.of(createBook(), createBook());

        // Mocking behavior
        when(bookRepository.findAllByCategories_Id(ID, pageable)).thenReturn(booksFromRepository);
        when(bookMapper.toDtoWithoutCategories(any(Book.class))).thenReturn(
                new BookDtoWithoutCategoryIds(ID, TITLE, AUTHOR, ISBN, PRICE, null, null));

        // When
        List<BookDtoWithoutCategoryIds> result = bookService.getByCategoryId(ID, pageable);

        // Then
        assertEquals(booksFromRepository.size(), result.size());

        // Verify method calls
        verify(bookRepository).findAllByCategories_Id(ID, pageable);
        verify(bookMapper, times(booksFromRepository.size())).toDtoWithoutCategories(
                any(Book.class));
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    private void mockingMapperMethods(Book book) {
        when(bookMapper.toModel(any(CreateBookRequestDto.class))).thenReturn(book);
        when(bookMapper.toDto(any(Book.class))).thenReturn(getBookDto(book));
    }

    private void mockingForUpdateMethod(Book existingBook, Set<Long> categoryIds) {
        when(bookRepository.findAllByIdOrIsbn(ID, ISBN)).thenReturn(List.of(existingBook));
        when(categoryService.getAllExistedCategoryIdsFromIds(CATEGORY_IDS)).thenReturn(
                categoryIds);
    }

    private void mockingForSaveMethod(CreateBookRequestDto requestDto, Set<Long> categoryIds) {
        when(bookRepository.findByIsbn(requestDto.isbn())).thenReturn(Optional.empty());
        when(categoryService.getAllExistedCategoryIdsFromIds(CATEGORY_IDS)).thenReturn(
                categoryIds);
    }

    private void verifySaveMethods() {
        verify(bookRepository).save(any(Book.class));
        verify(categoryService).getAllExistedCategoryIdsFromIds(CATEGORY_IDS);
    }

    private void verifyMapperMethods() {
        verify(bookMapper).toModel(any(CreateBookRequestDto.class));
        verify(bookMapper).toDto(any(Book.class));
    }
}
