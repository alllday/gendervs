package gendervs.gendervs1.domain.enums;

import lombok.Getter;

/**
 * 정렬 기준 Enum
 * 논제 목록 정렬 순서 지정
 */
@Getter
public enum SortBy {
    LATEST("최신순"),
    RECOMMEND("추천순"),
    VIEW("조회순"),
    POST("게시글순"),
    PARTICIPATE("참여순");

    private final String displayName;

    SortBy(String displayName) {
        this.displayName = displayName;
    }

}
