package capi.funding.api.infra.exceptions;

public class MilestoneSequenceException extends RuntimeException {
    public MilestoneSequenceException() {
        super();
    }

    public MilestoneSequenceException(String message) {
        super(message);
    }
}
