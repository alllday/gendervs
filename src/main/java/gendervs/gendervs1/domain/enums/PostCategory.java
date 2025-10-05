package gendervs.gendervs1.domain.enums;

import lombok.Getter;

@Getter
public enum PostCategory {  // 추후에 필요에 따라 카테고리 추가 가능
    TOPIC("논제"),
    NOTICE("공지"),
    SUGGESTION("건의");

    private final String displayName;

    PostCategory(String displayName) {
        this.displayName = displayName;
    }
}
