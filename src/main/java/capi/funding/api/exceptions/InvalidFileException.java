package capi.funding.api.exceptions;

public class InvalidFileException extends RuntimeException {
    public InvalidFileException() {
        super();
    }

    public InvalidFileException(String message) {
        super(message);
    }
}
