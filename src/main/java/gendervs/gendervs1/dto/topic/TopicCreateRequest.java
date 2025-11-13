package gendervs.gendervs1.dto.topic;

import gendervs.gendervs1.domain.enums.TopicCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TopicCreateRequest {

    @NotBlank(message = "논제 제목은 필수입니다")
    @Size(max = 100, message = "논제 제목은 100자 이하여야 합니다")
    private String title;

    @NotBlank(message = "논제 설명은 필수입니다")
    private String description;

    @NotNull(message = "논제 카테고리는 필수입니다")
    private TopicCategory topicCategory;
}
