package capi.funding.api.infra.exceptions;

public class InvalidParametersException extends RuntimeException {
    public InvalidParametersException() {
        super();
    }

    public InvalidParametersException(String message) {
        super(message);
    }
}
