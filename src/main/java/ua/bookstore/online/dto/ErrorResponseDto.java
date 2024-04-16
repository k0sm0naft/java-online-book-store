package ua.bookstore.online.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponseDto {
    private LocalDateTime timeStamp;
    private String status;
    private String error;
}
