package gendervs.gendervs1.domain.enums;

import lombok.Getter;

@Getter
public enum TermsType {     // 나중에 약관 만들때 제대로 만들기

    SERVICE("서비스 이용약관"),
    PRIVACY("개인정보 처리방침"),
    MARKETING("마케팅 수신 동의");

    private final String displayName;

    TermsType(String displayName) {
        this.displayName = displayName;
    }
}
