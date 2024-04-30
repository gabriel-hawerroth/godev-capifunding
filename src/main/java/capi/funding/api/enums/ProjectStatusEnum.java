package capi.funding.api.enums;

import lombok.Getter;

@Getter
public enum ProjectStatusEnum {

    IN_PLANNING(1),
    AWAITING_FUNDING(2),
    IN_PROGRESS(3),
    PAUSED(4),
    IN_REVIEW(5),
    DONE(6),
    CANCELED(7);

    private final Integer value;

    ProjectStatusEnum(Integer value) {
        this.value = value;
    }
}
