package ua.bookstore.online.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class BookSearchParameters extends AbstractSearchParameters {
    private final String[] titles;
    private final String[] authors;
    private final String[] isbns;
}
