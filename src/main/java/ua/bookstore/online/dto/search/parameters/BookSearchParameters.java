package ua.bookstore.online.dto.search.parameters;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class BookSearchParameters extends AbstractSearchParameters {
    @Schema(description = "List of titles (can be only partial)", example = "book")
    private final String[] titles;
    @Schema(description = "List of category IDs", example = "[2, 5, 3]")
    private final String[] categoryIds;
    @Schema(description = "List of author (can be only partial)", example = "auth")
    private final String[] authors;
    @Schema(description = "List of ISBN (can be only partial)", example = "061-964")
    private final String[] isbns;
    @Schema(description = "Minimal price for filtering", example = "12")
    private final Long minPrice;
    @Schema(description = "Maximal price for filtering", example = "999999999.99")
    private final Long maxPrice;
}
