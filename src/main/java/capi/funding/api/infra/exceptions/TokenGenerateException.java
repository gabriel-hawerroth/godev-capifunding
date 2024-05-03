package capi.funding.api.infra.exceptions;

public class TokenGenerateException extends RuntimeException {
    public TokenGenerateException() {
        super();
    }

    public TokenGenerateException(String message) {
        super(message);
    }
}
