package aiss.bitbucketminer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Error en la petición a Bitbucket API")
public class BitbucketApiException extends RuntimeException {

    public BitbucketApiException(String message) {
        super(message);
    }

    public BitbucketApiException() {
        super("Error en la petición a Bitbucket API");
    }
}
