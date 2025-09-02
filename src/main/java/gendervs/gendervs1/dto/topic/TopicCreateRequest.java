package gendervs.gendervs1.dto.topic;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TopicCreateRequest {
    
    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 100, message = "제목은 100자를 초과할 수 없습니다")
    private String title;
    
    @NotBlank(message = "설명은 필수입니다")
    private String description;
    
    @NotBlank(message = "카테고리는 필수입니다")
    @Size(max = 20, message = "카테고리는 20자를 초과할 수 없습니다")
    private String category;
    
    @NotBlank(message = "포지션 A는 필수입니다")
    @Size(max = 20, message = "포지션 텍스트는 20자를 초과할 수 없습니다")
    private String positionA;
    
    @NotBlank(message = "포지션 B는 필수입니다")
    @Size(max = 20, message = "포지션 텍스트는 20자를 초과할 수 없습니다")
    private String positionB;
    
    @NotBlank(message = "포지션 C는 필수입니다")
    @Size(max = 20, message = "포지션 텍스트는 20자를 초과할 수 없습니다")
    private String positionC;
}