package ua.bookstore.online.repository.book;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BookSearchParameter {
    TITLE("title"),
    CATEGORY("categories"),
    AUTHOR("author"),
    ISBN("isbn"),
    PRICE("price");

    private final String name;
}
