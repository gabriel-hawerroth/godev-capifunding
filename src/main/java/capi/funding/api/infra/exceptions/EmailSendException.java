package capi.funding.api.infra.exceptions;

public class EmailSendException extends RuntimeException {
    public EmailSendException() {
        super();
    }

    public EmailSendException(String message) {
        super(message);
    }
}
