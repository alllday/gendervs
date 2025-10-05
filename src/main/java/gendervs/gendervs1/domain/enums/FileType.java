package gendervs.gendervs1.domain.enums;

import lombok.Getter;

@Getter
public enum FileType {
    IMAGE("이미지"),
    VIDEO("비디오");

    private final String displayName;

    FileType(String displayName) {
        this.displayName = displayName;
    }
}
