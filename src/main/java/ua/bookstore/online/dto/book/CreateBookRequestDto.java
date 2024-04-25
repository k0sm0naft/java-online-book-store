package ua.bookstore.online.dto.book;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Set;

public record CreateBookRequestDto(
        @Schema(example = "Title of the book", nullable = true)
        @NotBlank
        String title,
        @Schema(example = "[1, 5 ,3]")
        @NotEmpty
        Set<Long> categoryIds,
        @Schema(example = "Book Author", nullable = true)
        @NotBlank
        String author,
        @Schema(example = "0-061-96436-0", nullable = true)
        @NotBlank
        String isbn,
        @NotNull
        @Min(0)
        @Schema(example = "25.99", nullable = true)
        BigDecimal price,
        @Schema(example = "About book")
        String description,
        @Schema(example = "https://example.com/cover-image.jpg")
        String coverImage
) {
}
