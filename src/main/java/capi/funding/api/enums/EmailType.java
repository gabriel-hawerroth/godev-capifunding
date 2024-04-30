package capi.funding.api.enums;

import lombok.Getter;

@Getter
public enum EmailType {
    ACTIVATE_ACCOUNT("activate-account");

    private final String value;

    EmailType(String value) {
        this.value = value;
    }
}
