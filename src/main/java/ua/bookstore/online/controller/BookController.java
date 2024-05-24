package ua.bookstore.online.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ua.bookstore.online.dto.book.BookDto;
import ua.bookstore.online.dto.book.CreateBookRequestDto;
import ua.bookstore.online.dto.search.parameters.BookSearchParameters;
import ua.bookstore.online.service.BookService;

@Tag(name = "Book management", description = "Endpoints for managing books")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/books")
public class BookController {
    private final BookService bookService;

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new book", description = "Create a new book if isbn uniq")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "401", description = "Required authorization",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "403", description = "Not enough access rights",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Conflict - the isbn non uniq",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public BookDto createBook(@RequestBody @Valid CreateBookRequestDto bookDto) {
        return bookService.save(bookDto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Update a book", description = "Update a book if exist")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Successfully updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "401", description = "Required authorization",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "403", description = "Not enough access rights",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Conflict - the isbn non uniq",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public BookDto updateBook(
            @PathVariable @Parameter(description = "Book ID") Long id,
            @RequestBody @Valid CreateBookRequestDto bookDto
    ) {
        return bookService.update(id, bookDto);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Return single book by id", description = "Return single book by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "401", description = "Required authorization",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Book with this id not exist",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public BookDto getBookById(@PathVariable @Parameter(description = "Book ID") Long id) {
        return bookService.getById(id);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Return page of books",
            description = "Return page of books with pagination and sorting")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "401", description = "Required authorization",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
    })
    public List<BookDto> getAll(
            @ParameterObject
            @PageableDefault(sort = {"price", "title"}, value = 5) Pageable pageable
    ) {
        return bookService.getAll(pageable);
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Return filtered page of books",
            description = "Return filtered page of books with pagination and sorting. "
                    + "Parameters: title, author, isbn, price")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "401", description = "Required authorization",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
    })
    public List<BookDto> searchBooks(
            @ParameterObject
            @PageableDefault(sort = {"price", "title"}, value = 5)Pageable pageable,
            BookSearchParameters bookSearchParameters
    ) {
        return bookService.getByParameters(bookSearchParameters, pageable);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a book by id", description = "Delete a book by id if exist")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No content - successfully deleted"),
            @ApiResponse(responseCode = "401", description = "Required authorization",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "403", description = "Not enough access rights",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Not found - wrong id",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public void deleteBook(@PathVariable @Parameter(description = "Book ID") Long id) {
        bookService.delete(id);
    }
}
