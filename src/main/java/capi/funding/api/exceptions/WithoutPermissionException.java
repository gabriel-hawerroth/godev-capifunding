package capi.funding.api.exceptions;

public class WithoutPermissionException extends RuntimeException {
    public WithoutPermissionException() {
        super();
    }

    public WithoutPermissionException(String message) {
        super(message);
    }
}
