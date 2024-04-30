package capi.funding.api.exceptions;

public class UnsendedEmailException extends RuntimeException {
    public UnsendedEmailException() {
        super();
    }

    public UnsendedEmailException(String message) {
        super(message);
    }
}
