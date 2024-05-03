package capi.funding.api.infra.exceptions;

public class DataIntegrityException extends RuntimeException {
    public DataIntegrityException() {
        super();
    }

    public DataIntegrityException(String message) {
        super(message);
    }
}
