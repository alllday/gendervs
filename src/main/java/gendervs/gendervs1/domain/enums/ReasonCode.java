package gendervs.gendervs1.domain.enums;

import lombok.Getter;

@Getter
public enum ReasonCode {
    PROFANITY("욕설/비방"),
    SPAM("도배/스팸"),
    ADULT_CONTENT("음란물"),
    FALSE_INFO("허위사실"),
    VIOLATION("규정위반"),
    ETC("기타");

    private final String displayName;

    private ReasonCode(String displayName) {
        this.displayName = displayName;
    }
}
