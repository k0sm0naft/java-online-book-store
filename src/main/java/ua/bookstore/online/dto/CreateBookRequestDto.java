package ua.bookstore.online.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CreateBookRequestDto(
        @Schema(description = "Book title", example = "Title of the book", nullable = true)
        @NotBlank
        String title,
        @Schema(description = "Author of the book", example = "Book Author", nullable = true)
        @NotBlank
        String author,
        @Schema(description = "Book isbn", example = "0-061-96436-0", nullable = true)
        @NotBlank
        String isbn,
        @NotNull
        @Min(0)
        @Schema(description = "Book price", example = "25.99", nullable = true)
        BigDecimal price,
        @Schema(description = "Book description", example = "About book")
        String description,
        @Schema(description = "Link on book cover", example = "https://example.com/cover-image.jpg")
        String coverImage
) {
}
