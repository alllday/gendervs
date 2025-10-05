package gendervs.gendervs1.domain.enums;

import lombok.Getter;

@Getter
public enum VoteType {
    LIKE("좋아요"),
    DISLIKE("싫어요");

    private final String value;

    VoteType(String value) {
        this.value = value;
    }
}
