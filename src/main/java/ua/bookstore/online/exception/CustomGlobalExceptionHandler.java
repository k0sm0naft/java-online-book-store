package ua.bookstore.online.exception;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import io.jsonwebtoken.JwtException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        List<String> errors = ex.getBindingResult().getAllErrors().stream()
                                .map(this::getErrorMessage)
                                .toList();
        return getResponseEntity(HttpStatus.valueOf(status.value()), errors);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        return getResponseEntity(HttpStatus.valueOf(status.value()), ex.getLocalizedMessage());
    }

    @ExceptionHandler(UniqueIsbnException.class)
    protected ResponseEntity<Object> handleUniqueIsbn(UniqueIsbnException ex) {
        return getResponseEntity(CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(RegistrationException.class)
    protected ResponseEntity<Object> handleRegistration(RegistrationException ex) {
        return getResponseEntity(CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<Object> handleNotFound(EntityNotFoundException ex) {
        return getResponseEntity(NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<Object> handleAccessDenied(AccessDeniedException ex) {
        return getResponseEntity(FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler({JwtException.class, AuthenticationException.class})
    protected ResponseEntity<Object> handleAuthenticationException(Exception ex) {
        return getResponseEntity(UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler({Exception.class})
    protected ResponseEntity<Object> handleNotIncludedExceptions(
            Exception ex) {
        return getResponseEntity(INTERNAL_SERVER_ERROR, ex.getLocalizedMessage());
    }

    private String getErrorMessage(ObjectError e) {
        if (e instanceof FieldError fieldError) {
            String field = fieldError.getField();
            String message = e.getDefaultMessage();
            return field + " " + message;
        }
        return e.getDefaultMessage();
    }

    private ResponseEntity<Object> getResponseEntity(HttpStatus status, Object error) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(status);
        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("error", error);
        detail.put("timestamp", LocalDateTime.now().toString());
        problemDetail.setProperties(detail);
        return ResponseEntity.of(problemDetail).build();
    }
}
