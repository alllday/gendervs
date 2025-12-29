package gendervs.gendervs1.domain.enums;

import lombok.Getter;

/**
 * 검색 필드 Enum
 * 논제 검색 시 어떤 필드를 대상으로 검색할지 지정
 */
@Getter
public enum SearchField {
    TITLE("제목"),
    CONTENT("내용"),
    AUTHOR("작성자");

    private final String displayName;

    SearchField(String value) {
        this.displayName = value;
    }
}
