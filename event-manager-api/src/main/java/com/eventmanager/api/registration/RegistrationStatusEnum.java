package com.eventmanager.api.registration;

import lombok.Getter;

public enum RegistrationStatusEnum {
    REGISTERED("registered"),
    PRESENT("present"),
    CANCELLED("cancelled");

    @Getter
    private final String value;

    RegistrationStatusEnum(String value) {
        this.value = value;
    }

    public boolean equals(String value) {
        return this.value.equals(value);
    }
}
