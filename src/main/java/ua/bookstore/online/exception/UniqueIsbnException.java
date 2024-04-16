package ua.bookstore.online.exception;

public class UniqueIsbnException extends RuntimeException {
    public UniqueIsbnException(String message) {
        super(message);
    }
}
