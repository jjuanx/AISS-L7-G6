package aiss.githubminer.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Failed to post project to GitMiner")
public class GitMinerPostException extends RuntimeException {
    public GitMinerPostException(String message) {
        super(message);
    }
}
