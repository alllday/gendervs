package gendervs.gendervs1.domain.enums;

import lombok.Getter;

@Getter
public enum ActionType { // 확장 가능
    DELETE("삭제"),
    ACTIVATE("활성화"),
    HIDE("숨김"),
    SUSPEND("정지"),
    WARN("경고");

    private final String displayName;

    ActionType(String displayName) {
        this.displayName = displayName;
    }
}
