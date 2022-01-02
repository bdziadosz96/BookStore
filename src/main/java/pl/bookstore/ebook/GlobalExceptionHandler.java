package pl.bookstore.ebook;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<?> handleMethodArgument(final MethodArgumentNotValidException e) {
    final List<String> errors =
        e.getBindingResult().getFieldErrors().stream()
            .map(x -> x.getField() + " - " + x.getDefaultMessage())
            .toList();
    return handleErrors(HttpStatus.BAD_REQUEST, errors);
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<?> handleIllegalState(final IllegalStateException e) {
    return handleErrors(HttpStatus.BAD_REQUEST, List.of(e.getMessage()));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<?> handleMessageNotReadable(final HttpMessageNotReadableException e) {
    return handleErrors(HttpStatus.BAD_REQUEST, Collections.singletonList(e.getMessage()));
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<?> handleEntityNotFoundException(final EntityNotFoundException e) {
    return handleErrors(HttpStatus.BAD_REQUEST, Collections.singletonList(e.getMessage()));
  }

  private ResponseEntity<Object> handleErrors(final HttpStatus status, final List<String> errors) {
    final Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", new Date());
    body.put("status", status.value());
    body.put("erros", errors);
    return new ResponseEntity<>(body, status);
  }
}
