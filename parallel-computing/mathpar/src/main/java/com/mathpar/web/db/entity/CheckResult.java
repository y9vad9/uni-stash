package com.mathpar.web.db.entity;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CheckResult {
    OK("YES"),
    GAVE_UP("GAVE UP"),
    WRONG("NO");

    private final String description;

    CheckResult(String description) {
        this.description = description;
    }

    @JsonValue
    public
    String getDescription() {
        return description;
    }
}
