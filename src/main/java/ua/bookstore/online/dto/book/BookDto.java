package ua.bookstore.online.dto.book;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

public record BookDto(
        @Schema(description = "Book ID", example = "ID of the book", nullable = true)
        Long id,
        @Schema(description = "Book title", example = "Title of the book", nullable = true)
        String title,
        @Schema(description = "Author of the book", example = "Book Author", nullable = true)
        String author,
        @Schema(description = "Book isbn", example = "0-061-96436-0", nullable = true)
        String isbn,
        @Schema(description = "Book price", example = "25.99", nullable = true)
        BigDecimal price,
        @Schema(description = "Book description", example = "About book")
        String description,
        @Schema(description = "Link on book cover", example = "https://example.com/cover-image.jpg")
        String coverImage
) {
}
