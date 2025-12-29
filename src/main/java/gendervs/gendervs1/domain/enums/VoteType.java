package gendervs.gendervs1.domain.enums;

import lombok.Getter;

@Getter
public enum VoteType {
    LIKE("좋아요"),
    DISLIKE("싫어요");

    private final String displayName;

    VoteType(String displayName) {
        this.displayName = displayName;
    }
}
