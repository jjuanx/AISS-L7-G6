package aiss.bitbucketminer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class BitbucketMinerRestExceptionHandler {

  @ExceptionHandler(InvalidParameterException.class)
  public ResponseEntity<Object> handleInvalidParameter(InvalidParameterException ex) {
    return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
  }

  @ExceptionHandler(RepositoryNotFoundException.class)
  public ResponseEntity<Object> handleRepositoryNotFound(RepositoryNotFoundException ex) {
    return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
  }

  @ExceptionHandler(BitbucketApiException.class)
  public ResponseEntity<Object> handleBitbucketApi(BitbucketApiException ex) {
    return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
  }

  // ⚙️ Método genérico para construir la respuesta
  private ResponseEntity<Object> buildErrorResponse(HttpStatus status, String message) {
    Map<String, Object> body = new HashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("status", status.value());
    body.put("error", status.getReasonPhrase());
    body.put("message", message);
    return new ResponseEntity<>(body, status);
  }
}
