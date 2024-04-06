package ua.bookstore.online.repository;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Parameter {
    TITLE("title"),
    AUTHOR("author"),
    ISBN("isbn"),
    DESCRIPTION("description");

    private final String name;
}
