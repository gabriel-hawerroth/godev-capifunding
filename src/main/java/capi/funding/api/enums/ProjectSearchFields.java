package capi.funding.api.enums;

import lombok.Getter;

@Getter
public enum ProjectSearchFields {
    PROJECT_TITLE("project_title"),
    PROJECT_CATEGORY("project_category"),
    PROJECT_STATUS("project_status"),
    CREATOR_NAME("creator_name");

    private final String value;

    ProjectSearchFields(String value) {
        this.value = value;
    }
}
