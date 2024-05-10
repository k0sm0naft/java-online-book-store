package ua.bookstore.online.dto.book;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.Set;
import lombok.Builder;

@Builder
public record BookDto(
        @Schema(example = "15", nullable = true)
        Long id,
        @Schema(example = "[1, 2, 5]")
        Set<Long> categoryIds,
        @Schema(example = "Title of the book", nullable = true)
        String title,
        @Schema(example = "Book Author", nullable = true)
        String author,
        @Schema(example = "0-061-96436-0", nullable = true)
        String isbn,
        @Schema(example = "25.99", nullable = true)
        BigDecimal price,
        @Schema(example = "About book")
        String description,
        @Schema(description = "Link on book cover", example = "https://example.com/cover-image.jpg")
        String coverImage
) {
}
