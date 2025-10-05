package gendervs.gendervs1.domain.enums;

import lombok.Getter;

@Getter
public enum TargetType {
    TOPIC("논제"),
    POST("게시글"),
    COMMENT("댓글"),
    USER("사용자");

    private final String displayName;

    TargetType(String displayName) {
        this.displayName = displayName;
    }
}
