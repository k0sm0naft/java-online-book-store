package ua.bookstore.online.exception;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ua.bookstore.online.dto.ErrorResponseDto;

@ControllerAdvice
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
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status);
        body.put("errors", errors);
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(UniqueIsbnException.class)
    protected ResponseEntity<ErrorResponseDto> handleMethodRepository(UniqueIsbnException ex) {
        return getResponseEntity(CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(RegistrationException.class)
    protected ResponseEntity<ErrorResponseDto> handleMethodRepository(RegistrationException ex) {
        return getResponseEntity(CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<ErrorResponseDto> handleMethodNotFound(EntityNotFoundException ex) {
        return getResponseEntity(NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<ErrorResponseDto> handleMethodNotFound(AccessDeniedException ex) {
        return getResponseEntity(FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler({SpecificationProviderNotFoundException.class})
    protected ResponseEntity<ErrorResponseDto> handleMethodSpecificationProviderNotFound(
            SpecificationProviderNotFoundException ex) {
        return getResponseEntity(INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    private String getErrorMessage(ObjectError e) {
        if (e instanceof FieldError fieldError) {
            String field = fieldError.getField();
            String message = e.getDefaultMessage();
            return field + " " + message;
        }
        return e.getDefaultMessage();
    }

    private ResponseEntity<ErrorResponseDto> getResponseEntity(HttpStatus status,
            String error) {
        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                                                         .timeStamp(LocalDateTime.now())
                                                         .status(status.getReasonPhrase())
                                                         .error(error)
                                                         .build();
        return new ResponseEntity<>(errorResponse, status);
    }
}
