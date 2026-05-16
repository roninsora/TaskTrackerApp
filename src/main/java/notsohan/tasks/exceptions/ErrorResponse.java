package notsohan.tasks.exceptions;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorResponse {
    private LocalDateTime timeStamp;
    private String message;
    private String details;

    public ErrorResponse(LocalDateTime timeStamp, String message, String details){
        this.timeStamp = timeStamp;
        this.message = message;
        this.details = details;
    }
}
