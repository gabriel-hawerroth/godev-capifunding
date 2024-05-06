package capi.funding.api.infra.exceptions;

public class ProjectEditabilityException extends RuntimeException {
    public ProjectEditabilityException(String message) {
        super(message);
    }
}
