package ua.bookstore.online.dto;

public record SearchParameters(
        String title,
        String author,
        String isbn,
        String description
) {
}
