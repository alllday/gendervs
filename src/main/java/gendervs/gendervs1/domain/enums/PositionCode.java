package gendervs.gendervs1.domain.enums;

import lombok.Getter;

@Getter
public enum PositionCode {
    POSITIVE("긍정"),
    NEGATIVE("부정"),
    NEUTRAL("중립");

    private final String displayName;

    PositionCode(String displayName) {
        this.displayName = displayName;
    }
}