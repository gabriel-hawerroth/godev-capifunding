package capi.funding.api.exceptions;

public class TokenCreationException extends RuntimeException {
    public TokenCreationException() {
        super();
    }

    public TokenCreationException(String message) {
        super(message);
    }
}
