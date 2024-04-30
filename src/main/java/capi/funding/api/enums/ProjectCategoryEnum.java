package capi.funding.api.enums;

import lombok.Getter;

@Getter
public enum ProjectCategoryEnum {

    TECHNOLOGY(1),
    GAMES(2),
    MEDICINE(3),
    ART(4),
    FASHION(5),
    NATURE(6),
    HEALTH(7),
    SPORTS(8);

    private final Integer value;

    ProjectCategoryEnum(Integer value) {
        this.value = value;
    }
}
