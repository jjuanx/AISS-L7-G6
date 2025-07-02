package aiss.bitbucketminer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Repositorio no encontrado en Bitbucket")
public class RepositoryNotFoundException extends RuntimeException {

    public RepositoryNotFoundException(String message) {
        super(message);
    }

    public RepositoryNotFoundException() {
        super("Repositorio no encontrado en Bitbucket");
    }
}

