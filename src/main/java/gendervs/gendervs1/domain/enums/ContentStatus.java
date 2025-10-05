package gendervs.gendervs1.domain.enums;

import lombok.Getter;

@Getter
public enum ContentStatus {
    ACTIVE("active"),
    DELETED("deleted"),
    HIDDEN("hidden");

    private final String displayName;

    ContentStatus(String displayName) {
        this.displayName = displayName;
    }
}
