package gendervs.gendervs1.domain.enums;

import lombok.Getter;

@Getter
public enum TopicCategory {
    MILITARY("국방/병역"),
    MARRIAGE_FAMILY("결혼/가족"),
    LAW_SYSTEM("법/제도"),
    POLITICS_ADMINISTRATION("정치/행정"),
    EMPLOYMENT_LABOR("고용/노동"),
    EDUCATION_BIRTH("교육/출산"),
    MEDIA_CULTURE("미디어/문화"),
    ETC("기타");

    private final String displayName;

    TopicCategory(String displayName) {
        this.displayName = displayName;
    }
}
