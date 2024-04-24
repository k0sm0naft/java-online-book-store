package ua.bookstore.online.dto.shopping.cart;

public record CartItemResponseDto(
        Long id,
        Long bookId,
        String bookTitle,
        int quantity
) {
}
