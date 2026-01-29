package gendervs.gendervs1.domain.enums;

import lombok.Getter;

@Getter
public enum Gender {
    M("남성"),
    F("여성");

    private final String displayName;

    Gender(String displayName) {
        this.displayName = displayName;
    }
}
