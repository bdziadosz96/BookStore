package pl.bookstore.ebook;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<?> handleException(MethodArgumentNotValidException e) {
    Map<String, Object> body = new LinkedHashMap<>();
    HttpStatus status = HttpStatus.BAD_REQUEST;
    body.put("timestamp", new Date());
    body.put("status", status.value());
    List<String> errors =
        e.getBindingResult().getFieldErrors().stream()
            .map(x -> x.getField() + " - " + x.getDefaultMessage())
            .toList();
    body.put("erros", errors);
    return new ResponseEntity<>(body, status);
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<?> handleIllegalStateException(IllegalStateException e) {
    Map<String, Object> body = new LinkedHashMap<>();
    HttpStatus status = HttpStatus.BAD_REQUEST;
    body.put("timestamp", new Date());
    body.put("status", status.value());
    body.put("errors", e.getLocalizedMessage());
    return new ResponseEntity<>(body, status);
  }

}
