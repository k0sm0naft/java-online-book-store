package ua.bookstore.online.utils;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;
import ua.bookstore.online.dto.book.BookDto;
import ua.bookstore.online.dto.book.CreateBookRequestDto;
import ua.bookstore.online.dto.category.CategoryResponseDto;
import ua.bookstore.online.model.Book;
import ua.bookstore.online.model.Category;

public class ConstantAndMethod {
    public static final Long ID = 1L;
    public static final String NON_EXISTING_ISBN = "1234";
    public static final String ISBN = "9780451524935";
    public static final Long NON_EXISTING_ID = 1234L;
    public static final String AUTHOR = "George Orwell";
    public static final String TITLE = "1984";
    public static final BigDecimal PRICE = BigDecimal.valueOf(12.99);
    public static final Set<Long> CATEGORY_IDS = Set.of(1L, 2L);
    public static final String ADD_THREE_BOOKS_SQL = "classpath:database/books/add-three-books.sql";
    public static final String ADD_CATEGORIES_SQL = "classpath:database/books/add-categories.sql";
    public static final String ADD_CATEGORIES_FOR_BOOKS_SQL =
            "classpath:database/books/add-categories-for-books.sql";
    public static final String ADD_THREE_CATEGORIES_SQL =
            "classpath:database/categories/add-three-categories.sql";
    public static final String TEAR_DOWN_DB_SQL = "database/tear-down-db.sql";

    public static CreateBookRequestDto createBookRequestDto() {
        return getRequestDto(TITLE);
    }

    public static CreateBookRequestDto getRequestDto(String title) {
        return CreateBookRequestDto.builder()
                                   .title(title)
                                   .author(AUTHOR)
                                   .isbn(ISBN)
                                   .categoryIds(CATEGORY_IDS)
                                   .price(PRICE)
                                   .build();
    }
    public static Book createBook() {
        Book book = new Book();
        book.setIsbn(ISBN);
        book.setAuthor(AUTHOR);
        book.setTitle(TITLE);
        book.setPrice(PRICE);
        book.setCategories(getCategories());
        return book;
    }

    public static BookDto getBookDto(Book book) {
        return BookDto.builder()
                      .categoryIds(
                              book.getCategories().stream()
                                  .map(Category::getId)
                                  .collect(Collectors.toSet()))
                      .title(book.getTitle())
                      .author(book.getAuthor())
                      .isbn(book.getIsbn())
                      .price(book.getPrice())
                      .build();
    }

    public static BookDto getOrwell(String title) {
        return BookDto.builder()
                      .id(ID)
                      .title(title)
                      .author(AUTHOR)
                      .isbn(ISBN)
                      .categoryIds(CATEGORY_IDS)
                      .price(PRICE)
                      .build();
    }

    public static BookDto getMelville() {
        return BookDto.builder()
                      .id(2L)
                      .title("Moby-Dick")
                      .author("Herman Melville")
                      .categoryIds(Set.of(1L, 3L))
                      .isbn("9781503280781")
                      .price(BigDecimal.valueOf(14.99))
                      .build();
    }

    public static Set<Category> getCategories() {
        return CATEGORY_IDS.stream()
                           .map(Category::new)
                           .collect(Collectors.toSet());
    }

    public static CategoryResponseDto getFiction() {
        return CategoryResponseDto.builder()
                                  .id(1L)
                                  .name("Fiction")
                                  .description("Fiction description")
                                  .build();
    }

    public static CategoryResponseDto getAdventure() {
        return CategoryResponseDto.builder()
                                  .id(2L)
                                  .name("Adventure")
                                  .description("Adventure description")
                                  .build();
    }

    public static CategoryResponseDto getClassic() {
        return CategoryResponseDto.builder()
                                  .id(3L)
                                  .name("Classic")
                                  .description("Classic description")
                                  .build();
    }
}
