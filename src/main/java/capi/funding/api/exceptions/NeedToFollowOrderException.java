package capi.funding.api.exceptions;

public class NeedToFollowOrderException extends RuntimeException {
    public NeedToFollowOrderException() {
        super();
    }

    public NeedToFollowOrderException(String message) {
        super(message);
    }
}
